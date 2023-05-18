function HomeTopProducts(){

  const topProducts = [
    {"id": 1, "name" : "Dummy Product 1", "animalType" : "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/420x260", "price": "\u20B91000"},
    {"id": 2, "name" : "Dummy Product 1", "animalType" : "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/420x260", "price": "\u20B92000"},
    {"id": 3, "name" : "Dummy Product 1", "animalType" : "Cat", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/420x260", "price": "\u20B92500"},
    {"id": 4, "name" : "Dummy Product 2", "animalType" : "Dog", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/420x260", "price": "\u20B9900"},
    {"id": 5, "name" : "Dummy Product 2", "animalType" : "Cat", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/420x260", "price": "\u20B9875"},
    {"id": 6, "name" : "Dummy Product 1", "animalType" : "Mouse", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/420x260", "price": "\u20B9300"},
    {"id": 7, "name" : "Dummy Product 3", "animalType" : "Fish", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/420x260", "price": "\u20B9200"},
    {"id": 8, "name" : "Dummy Product 3", "animalType" : "Mouse", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/420x260", "price": "\u20B91000"}
  ];

  const renderedElements = topProducts.map((element) => {
      return(
      <div className="lg:w-1/4 md:w-1/2 p-4 w-full" key={element.id}>
        <a className="block relative h-48 rounded overflow-hidden">
          <img alt="ecommerce" className="object-cover object-center w-full h-full block" src={element.image} />
        </a>
        <div className="mt-4">
          <h3 className="text-gray-500 text-xs tracking-widest title-font mb-1">{element.animalType} - {element.categoryName}</h3>
          <h2 className="text-gray-900 title-font text-lg font-medium">{element.name}</h2>
          <p className="mt-1">{element.price}</p>
        </div>
      </div>
      )
    })

return (
<section className="text-gray-600 body-font">
<h1 className="sm:text-3xl text-2xl font-medium title-font text-gray-900 items-center justify-center flex">Top Products</h1>
  <div className="container px-5 py-14 mx-auto">
    <div className="flex flex-wrap -m-4" id="topProducts">
      {renderedElements}
    </div>
  </div>
</section>
);
}
export default HomeTopProducts;