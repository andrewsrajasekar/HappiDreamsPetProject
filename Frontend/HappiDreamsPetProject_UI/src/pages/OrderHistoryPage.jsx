const orders = [{
  id: 1,
  date: '2023-05-15',
  totalAmount: 30,
  items:
  [{
    id: 1,
    date: '2023-05-15',
    price: 49.99,
    status: 'Delivered',
    image: 'https://dummyimage.com/150',
  },
  {
    id: 2,
    date: '2023-05-10',
    price: 79.99,
    status: 'Delivered',
    image: 'https://dummyimage.com/150',
  },
  {
    id: 3,
    date: '2023-05-05',
    price: 29.99,
    status: 'Cancelled',
    image: 'https://dummyimage.com/150',
  }]
}];
import React, { useState, useEffect } from "react";

function OrderHistoryPage() {
  const [pastOrders, setPastOrders] = useState([]);

  useEffect(() => {
    setPastOrders(orders);
  }, []);

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6">Past Orders</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {pastOrders.map((order) => (
          <div key={order.id} className="bg-white rounded shadow p-4">
            <h2 className="text-lg font-semibold mb-2">Order ID: {order.id}</h2>
            <p className="text-gray-500 text-sm mb-4">
              Date: {order.date}
            </p>
            <ul>
              {order.items.map((item) => (
                <li key={item.id} className="flex items-center mb-2">
                  <img
                    src={item.image}
                    alt={item.name}
                    className="w-12 h-12 object-cover rounded"
                  />
                  <div className="ml-2">
                    <h3 className="text-sm font-semibold">{item.name}</h3>
                    <p className="text-gray-500 text-xs">
                      Price: {item.price}
                    </p>
                  </div>
                </li>
              ))}
            </ul>
            <p className="mt-4">
              Total Amount: {order.totalAmount}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default OrderHistoryPage;

