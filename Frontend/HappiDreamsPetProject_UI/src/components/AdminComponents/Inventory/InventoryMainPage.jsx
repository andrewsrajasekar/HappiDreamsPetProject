import {MinusIcon, ChevronRightIcon}  from '@heroicons/react/20/solid';
import { useState } from 'react';
import { Transition } from 'react-transition-group';
import InventoryComponent from './InventoryComponent';
import AnimalCreateForm from './AnimalCreateForm';
import CategoryCreateForm from './CategoryCreateForm';
import ProductCreateForm from './ProductCreateForm';
import AnimalList from './AnimalList';
import CategoryList from './CategoryList';
import TabBar from '../../TabBar';
import ProductList from './ProductList';

function InventoryMainpage(){

    const [selectedAnimalType, setSelectedAnimalType] = useState({id: -1, name: "Select a Animal"});
    const [selectedCategory, setSelectedCategory] = useState({id: -1, name: "Select a Category"});
    const [isAnimalComponentShow, setAnimalComponentShow] = useState(false);
    const [isAnimalTransitionStart, setIsAnimalTransitionStart] = useState(false);
    const [isCategoryComponentShow, setCategoryComponentShow] = useState(false);
    const [isCategoryTransitionStart, setIsCategoryTransitionStart] = useState(false);
    const [removeFlexClass, setRemoveFlexClass] = useState(false);
    const [renderedTabContent, setRenderedTabContent] = useState();
    const animalTabs = [
      {
      "id": 0,
      "label": "Create a Animal Type",
      "handleOnClick": () => {
        return(
          <div className='mx-96'>
          <AnimalCreateForm />
          </div>
        )
      }
      },
      {
        "id": 1,
        "label": "Modify a Animal Type",
        "handleOnClick": () => {
          return(
            <AnimalList />
          )
        },
        "removeFlex": true
      }
  ]
    const categoryTabs = [
      {
      "id": 0,
      "label": `Create a Category for ${selectedAnimalType.name}`,
      "handleOnClick": () => {
        return(
          <div className='mx-96'>
          <CategoryCreateForm animalName={selectedAnimalType.name} />
          </div>
        )
      }
      },
      {
        "id": 1,
        "label": `Modify a Category for ${selectedAnimalType.name}`,
        "handleOnClick": () => {
          return(
            <CategoryList />
          )
        },
        "removeFlex": true
      }
  ]

  const productTabs = [
    {
    "id": 0,
    "label": `Create a Product for ${selectedAnimalType.name} - ${selectedCategory.name}`,
    "handleOnClick": () => {
      return(
        <div className='mx-96'>
        <ProductCreateForm />
        </div>
      )
    }
    },
    {
      "id": 1,
      "label": `Modify a Category for ${selectedAnimalType.name} - ${selectedCategory.name}`,
      "handleOnClick": () => {
        return(
          <ProductList categoryName={"Dummy Category 1"} animalName={"Dog"} />
        )
      },
      "removeFlex": true
    }
    ]

    const handleTabClick = (content, tabData) => {
      if(tabData.hasOwnProperty("removeFlex") && tabData.removeFlex){
        setRemoveFlexClass(true);
      }else{
        setRemoveFlexClass(false);
      }
      setRenderedTabContent(content);
    }

    const openAnimalPage = () => {
        setIsAnimalTransitionStart(true);
    }
    const openCategoryPage = () => {
      setIsCategoryTransitionStart(true);
    }
    return(
        <div className='flex-grow'>
            <Transition
        in={isAnimalTransitionStart}
        timeout={300}
        mountOnEnter
        unmountOnExit
        classNames="fade"
        onEntered={() => setAnimalComponentShow(true)}
        onExited={() => setAnimalComponentShow(false)}
      >
        {(state) => (
          <div
            className={`transition-transform duration-300 transform h-full ${
              state === 'entered' ? 'translate-y-0' : '-translate-y-full'
            }`}
          >
            <InventoryComponent tabs={animalTabs} selectedOption={selectedAnimalType.id >= 0 ? selectedAnimalType : undefined} inventoryType={"animal"} handleSave={(data) => {setSelectedAnimalType(data); console.log(data); setIsAnimalTransitionStart(false);}} />
          </div>
        )}
      </Transition>

      <Transition
        in={isCategoryTransitionStart}
        timeout={300}
        mountOnEnter
        unmountOnExit
        classNames="fade"
        onEntered={() => setCategoryComponentShow(true)}
        onExited={() => setCategoryComponentShow(false)}
      >
        {(state) => (
          <div
            className={`transition-transform duration-300 transform h-full ${
              state === 'entered' ? 'translate-y-0' : '-translate-y-full'
            }`}
          >
            <InventoryComponent tabs={categoryTabs} selectedOption={selectedCategory.id >= 0 ? selectedCategory : undefined} inventoryType={"category"} handleSave={(data) => {setSelectedCategory(data); console.log(data); setIsCategoryTransitionStart(false);}} />
          </div>
        )}
      </Transition>


        {!(isAnimalComponentShow && isCategoryComponentShow) &&
                <div className='flex items-center justify-center p-10'>
                <span className='flex flex-row'>
                    <h3 className='font-bold font-medium cursor-pointer text-purple-600 hover:text-purple-900' onClick={openAnimalPage}>{selectedAnimalType.name}</h3>
                    <MinusIcon className='w-6 -mr-[12px] mt-0.5 ml-2' />
                    <MinusIcon className='-mr-[12px] w-6 mt-0.5' />
                    <MinusIcon className='-mr-[12px] w-6 mt-0.5' />
                    <MinusIcon className='-mr-[12px] w-6 mt-0.5' />
                    <ChevronRightIcon className='-mr-[12px] w-6 mt-0.5' />
                </span>
                <span className='flex flex-row ml-4'>
                    <h3 className={`font-bold font-medium ${selectedAnimalType.id >= 0 ? "cursor-pointer text-purple-600 hover:text-purple-900" : "cursor-not-allowed text-gray-400"}`} disabled={selectedAnimalType.id < 0} onClick={selectedAnimalType.id < 0 ?  null : openCategoryPage}>{selectedCategory.name}</h3>
                </span>
            </div>
        }
        {selectedAnimalType.id >= 0 && selectedCategory.id >= 0 && 
          <div className="flex flex-col mx-32">
          <TabBar tabs={productTabs} onTabClick={(content, tabData) => {handleTabClick(content, tabData)}} />
          <div className={`mt-10 mb-10 ${removeFlexClass ? "" : "flex items-center justify-center"}`}>
            {renderedTabContent}
          </div>
        </div>
        }
        </div>
    );
}

export default InventoryMainpage;