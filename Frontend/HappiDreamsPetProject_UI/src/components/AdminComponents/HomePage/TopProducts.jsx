import { useEffect, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import Products from "../../../pages/Products";
import Loading from "../../Loading";
import Select from "react-select";

function TopProducts() {
  const [products, setProducts] = useState([
    {"id": 1, "name" : "Dummy Product 1", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/600x600", "price": "\u20B91000"},
    {"id": 2, "name" : "Dummy Product 2", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/600x600", "price": "\u20B92000"},
    {"id": 3, "name" : "Dummy Product 3", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/600x600", "price": "\u20B92500"},
    {"id": 4, "name" : "Dummy Product 4", "animalType": "Dog", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/600x600", "price": "\u20B9900"},
    {"id": 5, "name" : "Dummy Product 5", "animalType": "Dog", "categoryName": "Dummy Category 2", "image": "https://dummyimage.com/600x600", "price": "\u20B9875"},
    {"id": 6, "name" : "Dummy Product 6", "animalType": "Dog", "categoryName": "Dummy Category 1", "image": "https://dummyimage.com/600x600", "price": "\u20B9300"},
    {"id": 7, "name" : "Dummy Product 7", "animalType": "Dog", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/600x600", "price": "\u20B9200"},
    {"id": 8, "name" : "Dummy Product 8", "animalType": "Dog", "categoryName": "Dummy Category 3", "image": "https://dummyimage.com/600x600", "price": "\u20B91000"}
  ]);

  const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, label: "Select a Animal Type"});
  const [selectedCategory, setSelectedCategory] = useState({id: -1, label: "Select a Category"});
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [isAnimalTypeDropdownOpen, setIsAnimalTypeDropdownOpen] = useState(false);
  const [checkBoxIdsArray, setCheckBoxIdsArray] = useState([]);
  const [isProductLoading, setIsProductLoading] = useState(false);
  const maximumProductsAdded = 8;
  const animalOptions = [
    { id: 1, label: 'Cat' },
    { id: 2, label: 'Dog' },
    { id: 3, label: 'Fish' }];
  const categoryOptions = [
    { id: 1, label: 'Dummy Category 1' },
    { id: 2, label: 'Dummy Category 2' },
    { id: 3, label: 'Dummy Category 3' }];


  useEffect(() => {
    // fetch products or initialize the state
  }, []);

  const setCheckBoxDataFromProducts = () => {
    let checkBoxArray = [];
    products.map((product) => {
      if(product.animalType === selectedAnimalType.label && product.categoryName === selectedCategory.label){
        checkBoxArray.push(product.id);
      }
    })
    setCheckBoxIdsArray(checkBoxArray);
  } 

  useEffect(() => {
   if(selectedAnimalType.id >= 0 && selectedCategory.id >= 0){
    setIsProductLoading(true);
    setCheckBoxDataFromProducts();
   }
   setIsProductLoading(false);
  }, [selectedAnimalType, selectedCategory, products]);

  const addProduct = (product) => {
    let updatedProducts = Array.from(products);
    const data = {animalType: selectedAnimalType.label, categoryName: selectedCategory.label};
    updatedProducts.push({...product, ...data});
    setProducts(updatedProducts);
  }

  const removeProduct = (productData) => {
    setProducts(prevProducts => prevProducts.filter((product) => {
      return !(product.id === productData.id && product.animalType === selectedAnimalType.label && product.categoryName === selectedCategory.label);
    }));
    setIsProductLoading(true);
  }

  const handleCheckBox = (checkboxToggle, product) => {
    if(checkboxToggle){
      addProduct(product);
    }else{
      removeProduct(product);
    }
  }

  const handleDragStart = (event, index) => {
    event.dataTransfer.setData("index", index.toString());
  };

  const handleDeleteImage = (productId) => {
    setIsProductLoading(true);
    setProducts(products.filter((product) => product.id !== productId));
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleSaveProgress = () => {
    // Save progress logic here
    console.log('Progress saved!');
  };

  const handleDrop = (event, newIndex) => {
    const draggedIndex = parseInt(event.dataTransfer.getData("index"), 10);
    const updatedProducts = Array.from(products);
    const [draggedProduct] = updatedProducts.splice(draggedIndex, 1);
    updatedProducts.splice(newIndex, 0, draggedProduct);
    setProducts(updatedProducts);
    console.log(updatedProducts);
  };

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      background: 'white', // Change background color when focused
      cursor: 'pointer'
    }),
    option: (provided, state) => ({
        ...provided,
        cursor: 'pointer'
      })
  };

  return (
    <div className="p-4 flex-grow">
      <h1 className="text-2xl font-bold mb-4 flex justify-center items-center">Top Products Customization</h1>
      <ul className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4 mb-4">
        {products.map((product, index) => (
          <li
            key={product.id}
            draggable
            onDragStart={(event) => handleDragStart(event, index)}
            onDragOver={handleDragOver}
            onDrop={(event) => handleDrop(event, index)}
            className="group block overflow-hidden"
          >
            <span className="group block overflow-hidden cursor-move">
              <div className="flex items-center justify-center">
                <ImageThumbnail
                  image={{ url: product.image, name: product.name }}
                  showName={false}
                />
              </div>

              <div className="relative bg-white pt-3">
              <h3 className="text-xs text-gray-700 flex items-center justify-center">
                  {product.animalType} - {product.categoryName}
                </h3>
                <h3 className="text-xs text-gray-700 flex items-center justify-center">
                  {product.name}
                </h3>
                <span className="text-gray-900 flex items-center justify-center">
                  {product.price}
                </span>
              </div>
              <div className="flex items-center justify-center mt-2">
              <button
          className={`bg-red-500 hover:bg-red-700 text-white font-bold text-xs px-2 py-1 rounded cursor-pointer`}
          onClick={() => {handleDeleteImage(product.id)}}
        >
          Delete
        </button>
              </div>
            </span>
          </li>
        ))}
      </ul>
      <div className={`${products.length >= maximumProductsAdded ? "relative bg-gray-200 p-4 rounded opacity-50" : ""}`}>
      <div className={`${products.length >= maximumProductsAdded ? "absolute inset-0 flex items-center justify-center z-30" : "hidden"}`}>
        <div className="text-black-700 text-center">
          <h2 className="text-lg font-bold">Only {maximumProductsAdded} are allowed in the Top Products Section</h2>
          <p className="text-lg font-bold mt-2">Delete Some Products to add more</p>
        </div>
      </div>
      <div className="flex items-center justify-center mt-8">Animal Type</div>
      <div className={`${products.length >= maximumProductsAdded ? "pointer-events-none" : ""} flex-grow flex items-center justify-center ${isAnimalTypeDropdownOpen ? "mb-32" : ""}`}>
  <Select options={animalOptions}
      placeholder={selectedAnimalType.label} onChange={(selectedValue) => {setSelectedAnimalType(selectedValue)}} styles={customStyles}  getOptionValue={(option) => option.id} value={animalOptions.find((c) => c === selectedAnimalType)} onMenuOpen={() => {setIsAnimalTypeDropdownOpen(true)}} onMenuClose={() => {setIsAnimalTypeDropdownOpen(false)}} />

      </div>
      
      <div className={`${selectedAnimalType.id >= 0 ? "" : "hidden"}`}>
      <div className="flex items-center justify-center mb-2 mt-8">Category</div>
      <div className={`flex-grow flex items-center justify-center ${isCategoryDropdownOpen ? "mb-32" : ""} ${products.length >= maximumProductsAdded ? "pointer-events-none" : ""}`}>
          
    <Select options={categoryOptions}
      placeholder={selectedCategory.label} onChange={(selectedValue) => {setIsProductLoading(true);setSelectedCategory(selectedValue);}} styles={customStyles}  getOptionValue={(option) => option.id} value={categoryOptions.find((c) => c === selectedCategory)} onMenuOpen={() => {setIsCategoryDropdownOpen(true)}} onMenuClose={() => {setIsCategoryDropdownOpen(false)}} />
      
      </div>
      </div>
      <div className={`${selectedCategory.id >= 0 && !isProductLoading ? "" : "hidden"}`}>
        {isProductLoading ? <Loading /> : <>
        <div className="flex items-center justify-center mb-2 mt-5">Products</div>
      <div className={`overflow-y-auto max-h-[600px] ${products.length >= maximumProductsAdded ? "pointer-events-none" : ""}`}>
      <Products checkedBoxIds={checkBoxIdsArray} handleCheckBox={(product, checkboxToggle) => {handleCheckBox(checkboxToggle, product)}} preventProductNavigation={true} hideTitleVisibility={true} hideSortVisibility={true} category_name_from_components={selectedCategory.label} animal_type_from_components={selectedAnimalType.label} />
      </div>
        </>}

      
      </div>
      </div>
      <div className="flex flex-wrap items-center justify-center mt-10">
      <button
          className="bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4"
          onClick={handleSaveProgress}
        >
          Save
        </button>
        </div>
    </div>
  );
}

export default TopProducts;
