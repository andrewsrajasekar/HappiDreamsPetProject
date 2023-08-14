import React from 'react';
import './OrderSummary.css';
import { ORDER_CATEGORY_TYPE, ORDER_CATEGORY_INDEX, getOrderCategoryType } from "../utils/OrderCategoryTypes";
import { useParams } from 'react-router-dom';

const OrderSummary = () => {
    const trackingNumber = 'ABC123XYZ';
    const courierWebsiteUrl = 'https://www.examplecourier.com';
    const orderStatus = 'Shipped';
    const orderNumber = useParams()["order_number"];
    

    const handleTrackOrder = () => {
        window.open(courierWebsiteUrl, '_blank');
    };

    const orderDetails = {
      id: orderNumber,
      invoice_generated: true,
      order_placed_timestamp: "May 19, 2023",
      products:[
        {
          "name": "Dummy Product 1",
          "description": "This durable and portable insulated tumbler will keep your beverage at the perfect temperature during your next adventure.",
          "image_url": "https://dummyimage.com/800x800",
          "delivery_address": "Dummy Address, Dummy State, Dummy Country 12345",
          "price": "\u20B91000",
          "current_status": "processing"
        },
        {
          "name": "Dummy Product 2",
          "description": "This durable and portable insulated tumbler will keep your beverage at the perfect temperature during your next adventure.",
          "image_url": "https://dummyimage.com/800x800",
          "delivery_address": "Dummy Address, Dummy State, Dummy Country 12345",
          "price": "\u20B95000",
          "current_status": "shipped"
        },
        {
          "name": "Dummy Product 3",
          "description": "This durable and portable insulated tumbler will keep your beverage at the perfect temperature during your next adventure.",
          "image_url": "https://dummyimage.com/800x800",
          "delivery_address": "Dummy Address, Dummy State, Dummy Country 12345",
          "price": "\u20B910000",
          "current_status": "placed"
        },
        {
          "name": "Dummy Product 4",
          "description": "This durable and portable insulated tumbler will keep your beverage at the perfect temperature during your next adventure.",
          "image_url": "https://dummyimage.com/800x800",
          "delivery_address": "Dummy Address, Dummy State, Dummy Country 12345",
          "price": "\u20B910000",
          "current_status": "cancelled"
        }
      ]
    }

    const getCurrentStatusWidth = (product) => {
      const orderCategoryType = getOrderCategoryType(product.current_status); 
      let index = 0;
      if(orderCategoryType){
        index = ORDER_CATEGORY_INDEX[orderCategoryType];
      }
      if(product.current_status === ORDER_CATEGORY_TYPE.ORDER_PLACED){
        return "5%"; 
      }else if(product.current_status !== ORDER_CATEGORY_TYPE.CANCELLED){
        return 100 / index + "%"; 
      }else{
        return '100%';
      }
    }

    const getCurrentStatus = (product) => {
      return product.current_status;
    }

    const getCurrentStatusbar = (product) => {
      const orderCategoryType = getOrderCategoryType(product.current_status); 
      let index = 3;
      if(orderCategoryType){
        index = ORDER_CATEGORY_INDEX[orderCategoryType];
      }
      return(
        <>
        <div className={`${index <= 3 ? "text-purple-600  opacity-100" : ""}`}>Order placed</div>
        {product.current_status !== ORDER_CATEGORY_TYPE.CANCELLED && 
        <>
          <div className={`${index <= 2 ? "text-purple-600  opacity-100" : ""} text-center`}>Processing</div>
          <div className={`${index <= 1 ? "text-purple-600  opacity-100" : ""} text-right`}>Shipped</div>
        </>
        }

        {product.current_status === ORDER_CATEGORY_TYPE.CANCELLED && 
        <>
          <div className={`${index <= 1 ? "text-purple-600  opacity-100" : ""} text-right`}>Cancelled</div>
        </>
        }

        </>
      );
    }

    return (
      <div className=''>
        <div className='mx-72'>
        <div className='max-w-8xl pt-8 pb-8'>
            <div className="px-0 justify-between items-baseline flex">
                <div className="flex items-baseline">
                    <h1 className="text-3xl orderSummary tracking-tight font-bold">Order #{orderDetails.id}</h1>
                    {orderDetails.invoice_generated &&                     
                    <span className="alignInvoice text-purple-600 block invoice font-medium text-sm cursor-pointer ml-2">
                        View invoice
                    <span aria-hidden="true"> →</span>
                    </span>
                    }

                </div>
                <p className="alignOrderPlace orderPlace text-sm">
                    Order placed
                 <time dateTime="2021-03-22" className="orderSummary font-medium ml-1">{orderDetails.order_placed_timestamp}</time>
                 </p>
                 </div>
        </div>
        <section className='mt-6'>

        <div>
        {orderDetails.products.map((product, index) => (
        <div key={index} className="border rounded-lg boxShadow bg-white mb-16">
        <div className="p-8 gap-x-8 grid-cols-12 grid ">
          <div className="flex col-span-7">
            <div className="w-40 h-40 static pb-0 rounded-lg overflow-hidden">
              <img src={`${product.image_url}`} alt="Insulated bottle with white base and black snap lid." className="w-full h-full static object-contain" />
            </div>
            <div className="mt-0 ml-6">
              <h3 className="orderSummary font-medium text-base">
                <span className='cursor-pointer'>{product.name}</span>
              </h3>
              <p className="orderSummary font-medium mt-2">{product.price}</p>
              <p className="text-gray-500 opacity-100 text-sm mt-3">{product.description}</p>
            </div>
          </div>
          <div className="mt-0 col-span-5 ">
            <dl className="text-sm gap-x-6 grid-cols-1 grid">
              <div>
                <dt className="text-gray-900  opacity-100 font-medium">Delivery address</dt>
                <dd className="text-gray-500  opacity-100 mt-3">
                  <span className="block">{product.delivery_address}</span>
                </dd>
              </div>
              {/* <div>
                <dt className="text-gray-900  opacity-100 font-medium">Shipping updates</dt>
                <dd className="text-gray-500  opacity-100 mt-3">
                  <p>f•••@example.com</p>
                  <p>1•••••••••40</p>
                  <button type="button" className="editBtn text-indigo-600  opacity-100 font-medium">Edit</button>
                </dd>
              </div> */}
            </dl>
          </div>
        </div>
        <div className="p-8 border-t">
          {/* <p className="bg-gray-900  opacity-100 font-medium text-sm">Preparing to ship on <time dateTime="2021-03-24">March 24, 2021</time>
          </p> */}
          <div className="lf" aria-hidden="true">
            <div className="bg-gray-200  opacity-100 rounded-full overflow-hidden">
              <div className="bg-purple-600  opacity-100 rounded-full h-2" style={{width: `calc(${getCurrentStatusWidth(product)})`}}></div>
            </div>
            <div className={`grid text-gray-600  opacity-100 font-medium text-sm ${getCurrentStatus(product) === ORDER_CATEGORY_TYPE.CANCELLED ? "grid-cols-2" : "grid-cols-3"} mt-6`}>
              {getCurrentStatusbar(product)}
            </div>
          </div>
        </div>
      </div>
        ))}

</div>
        </section>
        </div>
        </div>
    );
};

export default OrderSummary;