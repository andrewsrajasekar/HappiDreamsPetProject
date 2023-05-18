import { useEffect, useState } from "react";
import Pagination from "../components/Pagination";
import { useNavigate, useParams } from 'react-router-dom';
import { CATEGORY_TYPE } from "../utils/CategoryTypes";

function Category({categoryType}){
    const { animal_type } = useParams();
    const isAnimalTypeCategory = (categoryType == CATEGORY_TYPE.ANIMAL_CATEGORY);
    const isAnimalProductTypeCategory = (categoryType == CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY);
    const navigate = useNavigate();
    const allAnimals = [
      {"id": 1, "name" : "Dummy Animal 1", "description": "Dummy Animal 1 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 2, "name" : "Dummy Animal 2", "description": "Dummy Animal 2 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 3, "name" : "Dummy Animal 3", "description": "Dummy Animal 3 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 4, "name" : "Dummy Animal 4", "description": "Dummy Animal 4 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 5, "name" : "Dummy Animal 5", "description": "Dummy Animal 5 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 6, "name" : "Dummy Animal 6", "description": "Dummy Animal 6 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 7, "name" : "Dummy Animal 7", "description": "Dummy Animal 7 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 8, "name" : "Dummy Animal 8", "description": "Dummy Animal 8 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 9, "name" : "Dummy Animal 9", "description": "Dummy Animal 9 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 10, "name" : "Dummy Animal 10", "description": "Dummy Animal 10 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 11, "name" : "Dummy Animal 11", "description": "Dummy Animal 11 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 12, "name" : "Dummy Animal 12", "description": "Dummy Animal 12 it is", "image": "https://dummyimage.com/600x360"},
      {"id": 13, "name" : "Dummy Animal 13", "description": "Dummy Animal 13 it is", "image": "https://dummyimage.com/600x360"}
    ];
    const allCategories = [
        {"id": 1, "name" : "Dummy Category 1", "description": "Dummy Category 1 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 2, "name" : "Dummy Category 2", "description": "Dummy Category 2 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 3, "name" : "Dummy Category 3", "description": "Dummy Category 3 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 4, "name" : "Dummy Category 4", "description": "Dummy Category 4 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 5, "name" : "Dummy Category 5", "description": "Dummy Category 5 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 6, "name" : "Dummy Category 6", "description": "Dummy Category 6 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 7, "name" : "Dummy Category 7", "description": "Dummy Category 7 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 8, "name" : "Dummy Category 8", "description": "Dummy Category 8 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 9, "name" : "Dummy Category 9", "description": "Dummy Category 9 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 10, "name" : "Dummy Category 10", "description": "Dummy Category 10 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 11, "name" : "Dummy Category 11", "description": "Dummy Category 11 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 12, "name" : "Dummy Category 12", "description": "Dummy Category 12 it is", "image": "https://dummyimage.com/600x360"},
        {"id": 13, "name" : "Dummy Category 13", "description": "Dummy Category 13 it is", "image": "https://dummyimage.com/600x360"}
      ];

      const [elements, setElements] = useState();

      const totalCategories = allCategories.length;

      const perPageCategories = 6;

      const totalPages = Math.floor(totalCategories / perPageCategories) + (totalCategories % perPageCategories == 0 ? 0 : 1);

      useEffect(() => {
        renderElements(isAnimalProductTypeCategory ? allCategories.slice(0, perPageCategories) : allAnimals.slice(0, perPageCategories));
      }, []);

      const onPageNumberChange = (pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => {
        renderElements(isAnimalProductTypeCategory ? allCategories.slice((pageNumber - 1) * perPageCategories, perPageCategories * pageNumber) : allAnimals.slice((pageNumber - 1) * perPageCategories, perPageCategories * pageNumber));
        setFromItem((pageNumber - 1) * perPageCategories + 1);
        setToItem(perPageCategories * pageNumber < totalCategories ? perPageCategories * pageNumber : totalCategories);
        setTotalPage(totalPages);
        setTotalData(totalCategories);
        return true;
      }

      const openProductsForCategories = (category) => {
        debugger;
        navigate("/" + animal_type + "/" + category["name"] + "/" + "products");
      }

      const openAnimalProductsForAnimal = (animal) => {
        navigate("/" + animal["name"] + "/" + "categories");
      }

      const renderElements = (categoriesData) => {
        setElements(
            categoriesData.map((data) => {
            return(
                <div className="lg:w-1/3 sm:w-1/2 p-4 cursor-pointer" key={data.id} onClick={() => {isAnimalProductTypeCategory ? openProductsForCategories(data) : openAnimalProductsForAnimal(data)}}>
        <div className="flex relative">
          <img alt="gallery" className="absolute inset-0 w-full h-full object-cover object-center" src={data.image} />
          <div className="px-8 py-10 relative z-10 w-full border-4 border-gray-200 bg-white opacity-0 hover:opacity-100">
            <h2 className="tracking-widest text-sm title-font font-medium text-indigo-500 mb-1">{data.name}</h2>
            {/* <h1 className="title-font text-lg font-medium text-gray-900 mb-3">Shooting Stars</h1> */}
            <p className="leading-relaxed">{data.description}</p>
          </div>
        </div>
      </div>
            )
        })
        );
      }


    return(
<section className="text-gray-600 body-font">
  <div className="container px-5 py-24 mx-auto">
    <div className="flex flex-col text-center w-full mb-12">
      <h1 className="sm:text-3xl text-2xl font-medium title-font text-gray-900">{isAnimalProductTypeCategory ? "Categories" : "Animal Types"}</h1>
    </div>
    <div className="flex flex-wrap -m-4">
      {elements}
    </div>
    <div className="mt-10">
    <Pagination totalPages={totalPages} onClickOfPageNumber={(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData) => onPageNumberChange(pageNumber, setFromItem, setToItem, setTotalPage, setTotalData)} initialPerPageResult={perPageCategories} totalResult={totalCategories} />
    </div>

  </div>
</section>
    );
}

export default Category;