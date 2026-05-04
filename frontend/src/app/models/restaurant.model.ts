export interface Restaurant {
  restaurantId: number;
  restaurantName: string;
  restaurantAddress: string;
  restaurantPhone: string;

  menuItemIds: number[];
  orderIds: number[];
  ratingIds: number[];
}