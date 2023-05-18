import { useEffect, useState } from "react";
import { PlusIcon, MinusIcon } from "@heroicons/react/20/solid";
import { ArrowLongLeftIcon } from "@heroicons/react/20/solid";
import { useNavigate } from "react-router-dom";
import AddressPage from "./AddressPage.jsx";
import OrderConfirmationPage from "./OrderConfirmationPage.jsx";

function Cart() {

  const [products, setProducts] = useState([]);
  const [renderedElements, setRenderedElements] = useState([]);
  let [total, setTotal] = useState(0);
  let [currency, setCurrency] = useState();
  const navigate = useNavigate();
  const [addressPageNavigate, setAddressPageNavigate] = useState(false);
  const [orderConfirmationPageNavigate, setOrderConfirmationPageNavigate] = useState(false);

  const goToCategoryPage = () => {
    navigate("/animals");
  }

  async function fetchProducts() {
    // const response = await fetch('https://example.com/products');
    // const data = await response.json();
    setProducts([{
      "id": 1,
      "animalType": "Dog",
      "category": "Dummy Category 1",
      "name": "Dummy Product 1",
      "color": "Red",
      "size": "Medium",
      "price": "58.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 4,
      "stock": 10
    },
    {
      "id": 2,
      "animalType": "Cat",
      "category": "Dummy Category 2",
      "name": "Dummy Product 2",
      "color": "Blue",
      "size": "Medium",
      "price": "28.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 2,
      "stock": 10
    },
    {
      "id": 3,
      "animalType": "Dog",
      "category": "Dummy Category 3",
      "name": "Dummy Product 2",
      "color": "Green",
      "size": "Small",
      "price": "20.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 1,
      "stock": 10
    },
    {
      "id": 4,
      "animalType": "Bird",
      "category": "Dummy Category 1",
      "name": "Dummy Product 2",
      "color": "Green",
      "size": "Medium",
      "price": "60.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 5,
      "stock": 10
    },
    {
      "id": 5,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    },
    {
      "id": 6,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    },
    {
      "id": 7,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    },
    {
      "id": 8,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    },
    {
      "id": 9,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    },
    {
      "id": 10,
      "animalType": "Cat",
      "category": "Dummy Category 1",
      "name": "Dummy Product 3",
      "color": "Blue",
      "size": "Large",
      "price": "100.00",
      "currency": "\u20B9",
      "images": [
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400',
        'https://dummyimage.com/400x400'
      ],
      "quantity": 3,
      "stock": 10
    }
    ]);
  }

  useEffect(() => {
    fetchProducts();
  }, []);

  useEffect(() => {
    total = 0;
    currency = "";
    setRenderedElements(
      products.map((element, index) => {
        total += element["price"] * element["quantity"];
        currency = element["currency"];
        return (
          <div key={element["id"]} className="flex items-center hover:bg-gray-100 -mx-8 px-6 py-5">
            <div className="flex w-2/5">
              <img className="max-w-24 max-h-24" src="https://dummyimage.com/400x400" alt="" />
              <div className="flex flex-col justify-between ml-4 flex-grow">
                <span className="font-bold text-sm">{element["name"]}</span>
                <span className="text-purple-600 text-xs">{element["animalType"]} - {element["category"]}</span>
                <span className="font-semibold hover:text-red-500 text-gray-500 text-xs cursor-pointer" onClick={() => { handleRemove(index) }}>Remove</span>
              </div>
            </div>
            <div className="flex justify-center w-1/5">
              <span className={`mr-2 inline-flex items-center text-xl font-bold ${element["quantity"] <= 1 ? "" : "cursor-pointer"}`} onClick={() => { element["quantity"] <= 1 ? null : handleQuantityDecrease(index) }} disabled={element["quantity"] <= 1}>
                <MinusIcon className="h-5 w-5 text-gray-600" />
              </span>

              {/* <span className="inline-flex items-center">{element["quantity"]}</span> */}
              <span className="border rounded-md shadow-sm inline-block w-8 px-3 py-2 text-gray-500">
                {element["quantity"]}
              </span>
              <span className={`ml-1 inline-flex items-center text-xl font-bold ${element["quantity"] >= element["stock"] ? "" : "cursor-pointer"}`} onClick={() => { element["quantity"] >= element["stock"] ? null : handleQuantityIncrease(index) }} disabled={element["quantity"] >= element["stock"]}>
                <PlusIcon className="h-5 w-5 text-gray-600" />
              </span>
            </div>

            <span className="text-center w-1/5 font-semibold text-sm">{element["currency"]}{element["price"]}</span>
            <span className="text-center w-1/5 font-semibold text-sm">{element["currency"]}{element["price"] * element["quantity"]}</span>
          </div>
        )
      })
    );

    setTotal(total);
    setCurrency(currency);
  }, [products]);

  const handleQuantityDecrease = (index) => {
    if (products[index]["quantity"] > 1) {
      products[index]["quantity"] = products[index]["quantity"] - 1;
      setProducts(products.slice());
    }
  };

  const handleQuantityIncrease = (index) => {
    products[index]["quantity"] = products[index]["quantity"] + 1;
    setProducts(products.slice());
  };

  const handleRemove = (index) => {
    let newArray = products.slice();
    newArray.splice(index, 1);
    setProducts(newArray);
  }

  const goToAddress = () => {
    setAddressPageNavigate(true);
    setOrderConfirmationPageNavigate(false);
  }

  const goToOrderConfirmPage = () => {
    setAddressPageNavigate(false);
    setOrderConfirmationPageNavigate(true);
  }

  return (
    <div>
      {!(addressPageNavigate || orderConfirmationPageNavigate) &&
        <div className="flex shadow-md">
          <div className="w-3/4 bg-white px-10 py-10 cart-list-container">
            <div className="flex justify-between border-b pb-8">
              <h1 className="font-semibold text-2xl">Shopping Cart</h1>
            </div>
            {products.length > 0 && <div className="flex mt-10 mb-5">
              <h3 className="font-semibold text-gray-600 text-xs uppercase w-2/5">Product Details</h3>
              <h3 className="font-semibold text-center text-gray-600 text-xs uppercase w-1/5 text-center">Quantity</h3>
              <h3 className="font-semibold text-center text-gray-600 text-xs uppercase w-1/5 text-center">Price</h3>
              <h3 className="font-semibold text-center text-gray-600 text-xs uppercase w-1/5 text-center">Total</h3>
            </div>}
            {products.length <= 0 && <div className="flex mt-10 mb-5 items-center justify-center">
              <h3 className="font-semibold text-gray-600 text-xs uppercase">No Products Found</h3>
            </div>}
            {renderedElements}

            <span onClick={goToCategoryPage} className="flex font-semibold text-indigo-600 text-sm mt-10 cursor-pointer">

              <ArrowLongLeftIcon className="fill-current mr-2 text-indigo-600 w-4" />
              Continue Shopping
            </span>
          </div>

          <div id="summary" className="w-1/4 px-8 py-10">
            <h1 className="font-semibold text-2xl border-b pb-8">Order Summary</h1>
            <div className="flex justify-between mt-10 mb-5">
              <span className="font-semibold text-sm uppercase">Total Items</span>
              <span className="font-semibold text-sm">{products.length}</span>
            </div>
            {/* <div>
        <label className="font-medium inline-block mb-3 text-sm uppercase">Shipping</label>
        <select className="block p-2 text-gray-600 w-full text-sm">
          <option>Standard shipping - $10.00</option>
        </select>
      </div> */}
            {/* <div className="py-10">
        <label for="promo" className="font-semibold inline-block mb-3 text-sm uppercase">Promo Code</label>
        <input type="text" id="promo" placeholder="Enter your code" className="p-2 text-sm w-full" />
      </div> */}
            {/* <button className="bg-red-500 hover:bg-red-600 px-5 py-2 text-sm text-white uppercase">Apply</button> */}
            <div className="border-t mt-8">
              <div className="flex font-semibold justify-between py-6 text-sm uppercase">
                <span>Total cost</span>
                <span>{currency}{total}</span>
              </div>
              <button className="bg-indigo-500 font-semibold hover:bg-indigo-600 py-3 text-sm text-white uppercase w-full" onClick={goToAddress}>Select Address</button>
            </div>
          </div>

        </div>
      }
      {addressPageNavigate &&
        <div>
          <AddressPage onCheckout={goToOrderConfirmPage} isCheckOutShown={true} fromPage={"Cart"} />
        </div>
      }
        {orderConfirmationPageNavigate &&
        <div>
          <OrderConfirmationPage />
        </div>
      }
    </div>
  );
}

export default Cart;