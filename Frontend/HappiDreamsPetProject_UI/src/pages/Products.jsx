import { event } from 'jquery';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
function Products(){
    const { category_name } = useParams();
    const { animal_type } = useParams();
    const [isAllDataInArrayUndefined, setIsAllDataInArrayUndefined] = useState(true);
    const [renderedElements, setRenderedElements] = useState();
    const [lowestPriceFilter, setLowestPriceFilter] = useState(-1);
    const [highestPriceFilter, setHighestPriceFilter] = useState(-1);
    const navigate = useNavigate();

    const handleChange=(event)=> {
        let value = event.target.value;
        if (!(/^[0-9]*$/.test(value))) {
          return;
        }
        if(event.target.id === "FilterPriceFrom"){
          setLowestPriceFilter(Number(value));
        }else if(event.target.id === "FilterPriceTo"){
          setHighestPriceFilter(Number(value));
        }
    };
  
    const handleKeyPress = (event) => {
      const keyCode = event.keyCode || event.which;
      const keyValue = String.fromCharCode(keyCode);
      if(keyValue === '\b'){
        return;
      }
      if (/[^0-9]/.test(keyValue) ) {
        event.preventDefault();
      }
    };

    const resetPriceFilter = () => {
      setLowestPriceFilter(-1);
      setHighestPriceFilter(-1);
    }

    const handleInputBlur = (event) => {
        if(event.target.id === "FilterPriceFrom" && lowestPriceFilter != ""){
          if(event.target.value > highestPriceFilter && highestPriceFilter != "" && highestPriceFilter >= 0){
            event.target.value = highestPriceFilter;
            setLowestPriceFilter(event.target.value);
          }
        }else if(event.target.id === "FilterPriceTo" && highestPriceFilter != ""){
          if(event.target.value < lowestPriceFilter && lowestPriceFilter != "" && lowestPriceFilter >= 0){
            event.target.value = lowestPriceFilter;
            setHighestPriceFilter(event.target.value);
          }
        }
    }

    const products = [
        {"id": 1, "name" : "Dummy Product 1", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/350x350", "price": "\u20B91000"},
        {"id": 2, "name" : "Dummy Product 1", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/350x350", "price": "\u20B92000"},
        {"id": 3, "name" : "Dummy Product 1", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/350x350", "price": "\u20B92500"},
        {"id": 4, "name" : "Dummy Product 2", "animalType": "Dog", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/350x350", "price": "\u20B9900"},
        {"id": 5, "name" : "Dummy Product 2", "animalType": "Dog", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/350x350", "price": "\u20B9875"},
        {"id": 6, "name" : "Dummy Product 1", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/350x350", "price": "\u20B9300"},
        {"id": 7, "name" : "Dummy Product 3", "animalType": "Dog", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/350x350", "price": "\u20B9200"},
        {"id": 8, "name" : "Dummy Product 3", "animalType": "Dog", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/350x350", "price": "\u20B91000"}
      ];
  
      const onProductClick = (product) => {
        console.log(product);
        navigate("/" + animal_type + "/" + category_name + "/" + product.id)
      }

      useEffect(() => {
        setRenderedElements(products.map((element) => {
        if(element["categoryName"] === category_name){
          if(isAllDataInArrayUndefined){
            setIsAllDataInArrayUndefined(false);
          }
            return(
              <li key={element.id}>
              <span href="#" className="group block overflow-hidden cursor-pointer" onClick={() => {onProductClick(element)}}>
                <img
                  src={element.image}
                  className="h-[350px] w-full object-cover transition duration-500 group-hover:scale-105 sm:h-[450px]"
                />
  
                <div className="relative bg-white pt-3">
                  <h3
                    className="text-xs text-gray-700 group-hover:underline group-hover:underline-offset-4"
                  >
                    {element.name}
                  </h3>
  
                  <p className="mt-2">
                    <span className="sr-only"> Regular Price </span>
  
                    <span className="tracking-wider text-gray-900">  {element.price} </span>
                  </p>
                </div>
              </span>
            </li>
                )
        }
      }
      ))
      
      if(isAllDataInArrayUndefined){
        setRenderedElements(<div>No Products Found</div>);
      }
      }, [isAllDataInArrayUndefined]);

  return (
    <section>
    <div className="mx-auto max-w-screen-xl px-4 py-8 sm:px-6 sm:py-12 lg:px-8">
      <header>
        <h2 className="text-xl font-bold text-gray-900 sm:text-3xl flex items-center justify-center mb-16">
          {animal_type} - {category_name} Collections
        </h2>
      </header>
  
      <div className="mt-8 block lg:hidden">
        <button
          className="flex cursor-pointer items-center gap-2 border-b border-gray-400 pb-1 text-gray-900 transition hover:border-gray-600"
        >
          <span className="text-sm font-medium"> Filters & Sorting </span>
  
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth="1.5"
            stroke="currentColor"
            className="h-4 w-4 rtl:rotate-180"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M8.25 4.5l7.5 7.5-7.5 7.5"
            />
          </svg>
        </button>
      </div>
  
      <div className="mt-4 lg:mt-8 lg:grid lg:grid-cols-4 lg:items-start lg:gap-8">
        <div className="hidden space-y-4 lg:block">
          <div>
            <label className="block text-xs font-medium text-gray-700">
              Sort By
            </label>
  
            <select id="SortBy" className="mt-1 rounded border-gray-300 text-sm">
              <option>Sort By</option>
              <option value="Title, DESC">Title, DESC</option>
              <option value="Title, ASC">Title, ASC</option>
              <option value="Price, DESC">Price, DESC</option>
              <option value="Price, ASC">Price, ASC</option>
            </select>
          </div>
  
          <div>
            <p className="block text-xs font-medium text-gray-700">Filters</p>
  
            <div className="mt-1 space-y-2">

  
              <details
                className="overflow-hidden rounded border border-gray-300 [&_summary::-webkit-details-marker]:hidden"
              >
                <summary
                  className="flex cursor-pointer items-center justify-between gap-2 p-4 text-gray-900 transition"
                >
                  <span className="text-sm font-medium"> Price </span>
  
                  <span className="transition group-open:-rotate-180">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                      className="h-4 w-4"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M19.5 8.25l-7.5 7.5-7.5-7.5"
                      />
                    </svg>
                  </span>
                </summary>
  
                <div className="border-t border-gray-200 bg-white">
                  <header className="flex items-center justify-between p-4">
                    <span className={`text-sm text-gray-700`}>

                      {lowestPriceFilter > 0 || highestPriceFilter > 0 ? "" : "No Price Filter Selected"}
                    </span>
  
                    <button
                      type="button"
                      className="text-sm text-gray-900 underline underline-offset-4 float-right"
                      onClick={resetPriceFilter}
                    >
                      Reset
                    </button>
                  </header>
  
                  <div className="border-t border-gray-200 p-4">
                    <div className="flex justify-between gap-4">
                      <label
                        className="flex items-center gap-2"
                      >
                        <span className="text-sm text-gray-600">$</span>
  
                        <input
                          type="text"
                          id="FilterPriceFrom"
                          placeholder="From"
                          value={lowestPriceFilter > 0 ? lowestPriceFilter : ""}
                          onChange={handleChange}
                          onKeyDown={handleKeyPress}
                          onBlur={handleInputBlur}
                          className="w-full sm:text-sm"
                        />
                      </label>
  
                      <label className="flex items-center gap-2">
                        <span className="text-sm text-gray-600">$</span>
  
                        <input
                          type="text"
                          id="FilterPriceTo"
                          placeholder="To"
                          value={highestPriceFilter > 0 ? highestPriceFilter : ""}
                          onChange={handleChange}
                          onKeyDown={handleKeyPress}
                          onBlur={handleInputBlur}
                          className="w-full sm:text-sm"
                        />
                      </label>
                    </div>
                  </div>
                </div>
              </details>

            </div>
          </div>
        </div>
  
        <div className="lg:col-span-3">
          <ul className={`${isAllDataInArrayUndefined ? "flex items-center justify-center" : "grid gap-4 sm:grid-cols-2 lg:grid-cols-3"}`}>
            {renderedElements}
          </ul>
        </div>
      </div>
    </div>
  </section>
  );
  }
  export default Products;