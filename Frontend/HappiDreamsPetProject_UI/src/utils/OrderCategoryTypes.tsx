export enum ORDER_CATEGORY_TYPE {
    ORDER_PLACED = 'placed',
    PROCESSING = 'processing',
    SHIPPED = 'shipped',
    CANCELLED = 'cancelled'
}

export enum ORDER_CATEGORY_INDEX {
    ORDER_PLACED = 3,
    PROCESSING = 2,
    SHIPPED = 1,
    CANCELLED = 1
  }
  
  export const getOrderCategoryType = (value: string): ORDER_CATEGORY_TYPE | undefined => {
    const keys = Object.keys(ORDER_CATEGORY_TYPE) as Array<ORDER_CATEGORY_TYPE>;
    return keys.find(key => ORDER_CATEGORY_TYPE[key] === value);
  };
