import { useEffect, useState } from "react";
import { getTopProducts } from "../services/ApiClient";

function HomeTopProducts() {

  const [topProducts, setTopProducts] = useState([]);
  const [renderedElements, setRenderedElements] = useState();
  const [isNoDataFound, setIsNoDataFound] = useState(true);
  useEffect(() => {
    const fetchData = async () => {
        const response = await getTopProducts();
        if(response.isSuccess && Array.isArray(response.successResponse.data.data) && response.successResponse.data.data.length > 0){
          if(response.successResponse.data.data.length > 0){
            setTopProducts(response.successResponse.data.data);
            setIsNoDataFound(false);
          }else{
            setTopProducts([]);
            setIsNoDataFound(true);
          }
        }else{
          setIsNoDataFound(true);
        }
    };

    fetchData();
  }, []);

  useEffect(() => {
    if (topProducts !== undefined) {
      setRenderedElements(
        topProducts.map((element) => {
          return (
            <div className="lg:w-1/4 md:w-1/2 p-4 w-full" key={element.id}>
              <a className="block relative h-48 rounded overflow-hidden">
                <img alt="ecommerce" className="object-cover object-center w-full h-full block" src={element.product.thumbnailImageUrl ? element.product.thumbnailImageUrl : "https://dummyimage.com/350x350"} />
              </a>
              <div className="mt-4">
                <h3 className="text-gray-500 text-xs tracking-widest title-font mb-1">{element.product.category.animal.name} - {element.product.category.name}</h3>
                <h2 className="text-gray-900 title-font text-lg font-medium">{element.product.name}</h2>
                <p className="mt-1">{element.product.price}</p>
              </div>
            </div>
          )
        })
      )
    }

  }, [topProducts])

  return (
    <section className="text-gray-600 body-font">
      {!isNoDataFound &&
        <>
          <h1 className="sm:text-3xl text-2xl font-medium title-font text-gray-900 items-center justify-center flex">Top Products</h1>
          <div className="container px-5 py-14 mx-auto">
            <div className="flex flex-wrap -m-4" id="topProducts">
              {renderedElements}
            </div>
          </div>
        </>
      }

    </section>
  );
}
export default HomeTopProducts;