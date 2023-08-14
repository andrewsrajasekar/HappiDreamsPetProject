import React, { useState } from 'react';
import Sidebar from '../components/Sidebar';
import MainBoard from '../components/AdminComponents/HomePage/MainBoard';
import TopProducts from '../components/AdminComponents/HomePage/TopProducts';
import HomeCategory from '../components/AdminComponents/HomePage/HomeCategory';
import InventoryMainpage from '../components/AdminComponents/Inventory/InventoryMainPage';
import AboutUs from '../components/AdminComponents/AboutUs/AboutUs';

function AdminPanel(){
    const [renderContent, setRenderContent] = useState((<><div className='text-2xl font-bold flex flex-grow justify-center items-center'>Welcome to Customization Page, Select a Category from Side to Start!</div></>));
  
    const tabs = [
        {
            label: "Home Page",
            childMenu: [
                {
                    label: "Main Board",
                    handleTabChange: function(){
                      setRenderContent(
                        <MainBoard />
                    );
                    }
                  },
                  {
                    label: "Top Products",
                    handleTabChange: function(){
                      setRenderContent(
                        <TopProducts />
                      );
                    }
                  },
                  {
                    label: "Category",
                    handleTabChange: function(){
                      setRenderContent(
                        <HomeCategory />
                      );
                    }
                  }
            ]
        },
        {
            label: "Inventory",
            handleTabChange: function(){
              setRenderContent(
                <InventoryMainpage />
            );
            }
        },
        {
            label: "About Us",
            childMenu: [
                {
                    label: "Brands Logo",
                    handleTabChange: function(){
                      setRenderContent(
                       <AboutUs />
                    );
                    }
                  }
            ]
        }
     
    ]
  
    return (
      <div className='flex flex-row'>
      <Sidebar tabs={tabs} />
      {renderContent}
      </div>
    );

}

export default AdminPanel;