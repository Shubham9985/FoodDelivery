export interface MenuItem {
  itemId: number;
  itemName: string;
  itemDescription: string;
  itemPrice: number;

  restaurantId: number;
  restaurantName: string;

  orderItemIds: number[];
}