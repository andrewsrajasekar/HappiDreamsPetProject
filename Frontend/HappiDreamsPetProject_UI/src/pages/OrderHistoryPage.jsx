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
import React from 'react';
import { useNavigate } from 'react-router-dom';

const OrderHistory = () => {
  const navigate = useNavigate();
  const goToOrderSummaryPage = (orderNumber) => {
    
    navigate("/orderhistory/" + orderNumber);
  }


  return (
    <div className="py-12">
      <div className='px-8 max-w-7xl mx-auto'>
        <div className='px-0 max-w-4xl mx-auto'>
          <h1 className="text-3xl bgopactext tracking-tight font-bold ">
            Order History
            </h1>
            <p className='bgopacdesc text-sm mt-2'>
            Check the status of recent orders, manage returns, and discover similar products.
            </p>
        </div>
      </div>

      <section className='mt-12'>
      <div className='px-8 max-w-7xl mx-auto'>
      <div className='px-0 max-w-4xl mx-auto'>
      {[1, 2, 3, 4, 5].map((orderNumber,index) => (
          <div id={`squarebox_${index}`} key={orderNumber} className="border rounded-lg buttonboxshadow bgopacoverall topCalc">

            <div className="p-6 gap-x-6 grid border-b items-center">
              <dl className='col-span-1 grid-cols-4 text-sm gap-x-6 grid '>
                <div>
                  <dt className='bgopactext font-medium'>
                  Order Number
                  </dt>
                  <dd className='bgopacdesc mt-1'>
                  {orderNumber}
                  </dd>
                </div>
                <div className='block'>
                  <dt className='bgopactext font-medium'>
                    Date Placed
                  </dt>
                  <dd className='bgopacdesc mt-1'>
                  {new Date().toLocaleDateString()}
                  </dd>
                </div>
                <div>
                <dt className='bgopactext font-medium'>
                    Date Placed
                  </dt>
                  <dd className='bgopactext font-medium mt-1'>
                  $100
                  </dd>
                </div>
                <div>
                <span className='buttonboxshadow bgopacbutton font-medium text-sm py-2 px-2.5 bgopacbutton2 border rounded-md justify-center cursor-pointer items-center flex' onClick={() => {goToOrderSummaryPage(orderNumber)}}>View Order</span>
              </div>
              </dl>


             
            </div>

          <ul>
            <li className='p-4 border-b'>

              <div className='items-start flex '>
                <div className='w-40 h-40 rounded-lg overflow-hidden shrink-0'>
              <img className="w-full h-full mr-4 object-center object-cover" src="https://dummyimage.com/400x400" alt="Product" />
              </div>
              <span className="ml-6 flex-1">
              <span className='flex items-center justify-between bgopactext font-medium'>
                <h5>Product Name</h5>
                <p className='mt-0'>Price: $50</p>
              </span>
              <span className='mt-2 block bgopacdesc'>
              <p>mi tempus imperdiet nulla malesuada pellentesque elit eget gravida cum sociis natoque penatibus et magnis dis parturient montes nascetur ridiculus mus mauris vitae ultricies leo integer malesuada nunc vel risus commodo viverra maecenas accumsan lacus vel facilisis volutpat est velit egestas dui id ornare arcu odio ut sem nulla pharetra diam sit amet nisl suscipit adipiscing bibendum est ultricies integer quis auctor elit sed vulputate mi sit amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero nunc consequat interdum varius sit amet mattis vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor posuere</p>
              </span>
              </span>
              </div>
              <div className='justify-between flex mt-6'>
                <div className='flex items-center'><p className='bgopacdesc font-medium text-sm ml-2'>Order Status</p></div>
           
              <div className='pt-0  border-none mt-0 ml-4 font-medium text-sm border-t items-center flex '>
              <div className="justify-center flex-1 flex"><span className='bgopaclink whitespace-nowrap cursor-pointer'>View Product</span></div>
              <div className='pl-4 justify-center flex-1 flex'><span className="bgopaclink whitespace-nowrap cursor-pointer">Buy Again</span></div>
              </div>
              </div>

              </li> 
              <li className='p-4'>

              <div className='items-start flex '>
                <div className='w-40 h-40 rounded-lg overflow-hidden shrink-0'>
              <img className="w-full h-full mr-4 object-center object-cover" src="https://dummyimage.com/400x400" alt="Product" />
              </div>
              <span className="ml-6 flex-1">
              <span className='flex items-center justify-between bgopactext font-medium'>
                <h5>Product Name</h5>
                <p className='mt-0'>Price: $50</p>
              </span>
              <span className='mt-2 block bgopacdesc'>
              <p>mi tempus imperdiet nulla malesuada pellentesque elit eget gravida cum sociis natoque penatibus et magnis dis parturient montes nascetur ridiculus mus mauris vitae ultricies leo integer malesuada nunc vel risus commodo viverra maecenas accumsan lacus vel facilisis volutpat est velit egestas dui id ornare arcu odio ut sem nulla pharetra diam sit amet nisl suscipit adipiscing bibendum est ultricies integer quis auctor elit sed vulputate mi sit amet mauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin libero nunc consequat interdum varius sit amet mattis vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor posuere</p>
              </span>
              </span>
              </div>
              <div className='justify-between flex mt-6'>
                <div className='flex items-center'><p className='bgopacdesc font-medium text-sm ml-2'>Order Status</p></div>
           
              <div className='pt-0  border-none mt-0 ml-4 font-medium text-sm border-t items-center flex '>
              <div className="justify-center flex-1 flex"><span className='bgopaclink whitespace-nowrap cursor-pointer'>View Product</span></div>
              <div className='pl-4 justify-center flex-1 flex'><span className="bgopaclink whitespace-nowrap cursor-pointer">Buy Again</span></div>
              </div>
              </div>

              </li> 
          </ul>
          </div>
      ))} 
      </div>
      </div>
      </section>
</div>
  );
};

export default OrderHistory;
