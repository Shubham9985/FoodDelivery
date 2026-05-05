const fs = require('fs');
const path = require('path');

function findFiles(dir, filter, fileList = []) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const stat = fs.statSync(path.join(dir, file));
    if (stat.isDirectory()) {
      findFiles(path.join(dir, file), filter, fileList);
    } else if (filter(file)) {
      fileList.push(path.join(dir, file));
    }
  }
  return fileList;
}

const specFiles = findFiles('./src/app', name => name.endsWith('.spec.ts'));

for (const file of specFiles) {
  let content = fs.readFileSync(file, 'utf8');
  if (content.includes('import { HttpClientTestingModule }') || content.includes('httpTestingController')) {
    continue; // already modified
  }
  
  if (content.includes('TestBed.configureTestingModule')) {
    let newImports = "import { HttpClientTestingModule } from '@angular/common/http/testing';\nimport { RouterTestingModule } from '@angular/router/testing';\nimport { FormsModule, ReactiveFormsModule } from '@angular/forms';\nimport { NoopAnimationsModule } from '@angular/platform-browser/animations';\n";
    content = newImports + content;
    
    // Replace empty TestBed config with imports
    content = content.replace(/TestBed\.configureTestingModule\(\{[\s\n]*\}\)/g, "TestBed.configureTestingModule({\n      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule, ReactiveFormsModule, NoopAnimationsModule]\n    })");
    
    // In case there is an imports array already and not caught by above
    if (content.includes('TestBed.configureTestingModule({') && !content.includes('imports: [HttpClientTestingModule')) {
        content = content.replace(/TestBed\.configureTestingModule\(\{/g, "TestBed.configureTestingModule({\n      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule, ReactiveFormsModule, NoopAnimationsModule],");
    }

    fs.writeFileSync(file, content);
  }
}
console.log('Fixed spec files');
