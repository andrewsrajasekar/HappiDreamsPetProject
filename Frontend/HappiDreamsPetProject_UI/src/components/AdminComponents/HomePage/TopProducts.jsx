import { useEffect, useState } from "react";
import ImageThumbnail from "../ImageThumbnail";
import Products from "../../../pages/Products";
import Loading from "../../Loading";
import Select from "react-select";
import { clearAndAddTopProductsInBulk, getAllAnimals, getAllCategories, getTopProducts } from "../../../services/ApiClient";
import UINotification from "../../UINotification";

function TopProducts() {
  const [products, setProducts] = useState([]);

  const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, name: "Select a Animal Type"});
  const [selectedCategory, setSelectedCategory] = useState({id: -1, name: "Select a Category"});
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [isAnimalTypeDropdownOpen, setIsAnimalTypeDropdownOpen] = useState(false);
  const [checkBoxIdsArray, setCheckBoxIdsArray] = useState([]);
  const [isProductLoading, setIsProductLoading] = useState(false);
  const maximumProductsAdded = 8;
  const [animals, setAnimals] = useState([]);
  const [categories, setCategories] = useState([]);
  const [isSaveButtonDisabled, setIsSaveButtonDisabled] = useState(true);
  const [refreshComponent, setRefreshComponent] = useState(true);

    const fetchTopProducts = async () => {
      const response = await getTopProducts();
      if(response.isSuccess && Array.isArray(response.successResponse.data.data)){
        if(response.successResponse.data.data.length > 0){
          setProducts(response.successResponse.data.data);
        }else{
          setProducts([]);
        }
      }else{
        UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
      }
  };

  const fetchAnimals = async() => {
    const allAnimalsData = await getAllAnimals();
    if(allAnimalsData.isSuccess){
      setAnimals(allAnimalsData.successResponse.data.data);
    }else{
      setAnimals([]);
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }

  const fetchCategory = async() => {
    const allCategoriesData = await getAllCategories(selectedAnimalType.id);
    if(allCategoriesData.isSuccess){
      setCategories(allCategoriesData.successResponse.data.data);
    }else{
      UINotification({ message: "Issue Occured, while collecting Categories Data", type: "Error" });
    }
  }

  useEffect(() => {
    if(refreshComponent){
      fetchTopProducts();
      fetchAnimals();
      setRefreshComponent(false);
    }
  }, [refreshComponent]);

  useEffect(() => {
    if(selectedAnimalType.id && selectedAnimalType.id > 0){
      fetchCategory();
    }
  }, [selectedAnimalType]);


  const setCheckBoxDataFromProducts = () => {
    let checkBoxArray = [];
    products.map((product) => {
      if(product.product.category.animal.id === selectedAnimalType.id && product.product.category.id === selectedCategory.id){
        checkBoxArray.push(product.product.id);
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
    setProducts(prevProducts => {
      const updatedProducts = [...prevProducts];
      const data = { category: selectedCategory };
      const newProduct = { ...product, ...data };
      let pushData = {};
      pushData.product = newProduct;
      updatedProducts.push(pushData);
      return updatedProducts;
    });
  }

  const removeProduct = (productData) => {
    setProducts(prevProducts => prevProducts.filter((product) => {
      return !(product.product.id === productData.id && product.product.category.animal.id === selectedAnimalType.id && product.product.category.id === selectedCategory.id);
    }));
  }

  const handleCheckBox = (checkboxToggle, product) => {
    if(checkboxToggle){
      addProduct(product);
    }else{
      removeProduct(product);
    }
    setIsSaveButtonDisabled(false);
  }

  const handleDragStart = (event, index) => {
    event.dataTransfer.setData("index", index.toString());
  };

  const handleDeleteImage = (productId) => {
    setIsProductLoading(true);
    setProducts(products.filter((product) => product.id !== productId));
    setIsSaveButtonDisabled(false);
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleSaveProgress = async() => {
    let apiProducts = products.map((product, index) => ({
      animal_id: product.product.category.animal.id,
      category_id: product.product.category.id,
      product_id: product.product.id,
      order_number: index + 1,
    }));
    const createTopProductsResponse = await clearAndAddTopProductsInBulk(apiProducts);
    if(createTopProductsResponse.isSuccess){
      setRefreshComponent(true);
      UINotification({ message: "Saved Successfully", type: "Success" });
      setIsSaveButtonDisabled(true);
    }else{
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
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

  const getOptionLabel = (option) => {
    if(option.hasOwnProperty("name")){
      return option.name;
    }else if(option.hasOwnProperty("label")){
      return option.label;
    }
    return null;
  }

  return (
    <div className="p-4 flex-grow">
      <h1 className="text-2xl font-bold mb-4 flex justify-center items-center">Top Products Customization</h1>
      <ul className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4 mb-4">
        {products.map((product, index) => (
          <li
            key={product.product.id}
            draggable
            onDragStart={(event) => handleDragStart(event, index)}
            onDragOver={handleDragOver}
            onDrop={(event) => handleDrop(event, index)}
            className="group block overflow-hidden"
          >
            <span className="group block overflow-hidden cursor-move">
              <div className="flex items-center justify-center">
                <ImageThumbnail
                  image={{ url: product.product.thumbnailImageUrl ? product.product.thumbnailImageUrl : "https://dummyimage.com/350x350", name: product.product.name }}
                  showName={false}
                />
              </div>

              <div className="relative bg-white pt-3">
              <h3 className="text-xs text-gray-700 flex items-center justify-center">
                  {product.product.category.animal.name} - {product.product.category.name}
                </h3>
                <h3 className="text-xs text-gray-700 flex items-center justify-center">
                  {product.product.name}
                </h3>
                <span className="text-gray-900 flex items-center justify-center">
                  {product.product.price}
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
  <Select options={animals}
      placeholder={selectedAnimalType.name} onChange={(selectedValue) => {setSelectedAnimalType(selectedValue)}} styles={customStyles} getOptionLabel={getOptionLabel}  getOptionValue={(option) => option.id} value={animals.find((c) => c.id === selectedAnimalType.id)} onMenuOpen={() => {setIsAnimalTypeDropdownOpen(true)}} onMenuClose={() => {setIsAnimalTypeDropdownOpen(false)}} />

      </div>
      
      <div className={`${selectedAnimalType.id >= 0 ? "" : "hidden"}`}>
      <div className="flex items-center justify-center mb-2 mt-8">Category</div>
      <div className={`flex-grow flex items-center justify-center ${isCategoryDropdownOpen ? "mb-32" : ""} ${products.length >= maximumProductsAdded ? "pointer-events-none" : ""}`}>
          
    <Select options={categories}
      placeholder={selectedCategory.name} onChange={(selectedValue) => {setIsProductLoading(true);setSelectedCategory(selectedValue);}} styles={customStyles} getOptionLabel={getOptionLabel}  getOptionValue={(option) => option.id} value={categories.find((c) => c.id === selectedCategory.id)} onMenuOpen={() => {setIsCategoryDropdownOpen(true)}} onMenuClose={() => {setIsCategoryDropdownOpen(false)}} />
      
      </div>
      </div>
      <div className={`${selectedCategory.id >= 0 && !isProductLoading ? "" : "hidden"}`}>
        {isProductLoading ? <Loading /> : <>
        <div className="flex items-center justify-center mb-2 mt-5">Products</div>
      <div className={`overflow-y-auto max-h-max ${products.length >= maximumProductsAdded ? "pointer-events-none" : ""}`}>
      {selectedAnimalType.id && selectedAnimalType.id > 0 && selectedCategory.id && selectedCategory.id > 0 && 
      <div>
        <Products isAdminPanelUsage={true} checkedBoxIds={checkBoxIdsArray} handleCheckBox={(product, checkboxToggle) => {handleCheckBox(checkboxToggle, product)}} preventProductNavigation={true} hideTitleVisibility={true} hideSortVisibility={true} category_info_from_components={selectedCategory} animal_info_from_components={selectedAnimalType} />
        </div>
      }
      </div>
        </>}

      
      </div>
      </div>
      <div className="flex flex-wrap items-center justify-center mt-10">
      <button
          className="disabled:opacity-25 disabled:cursor-not-allowed bg-green-500 w-36 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mt-4"
          onClick={handleSaveProgress}
          disabled={isSaveButtonDisabled}
        >
          Save
        </button>
        </div>
    </div>
  );
}

export default TopProducts;
