import React, { useEffect, useState } from 'react';

function TabBar({tabs, onTabClick}) {
  const [activeTab, setActiveTab] = useState(0);

  const handleTabClick = (tabData, tabIndex) => {
    setActiveTab(tabIndex);
    if(tabData.hasOwnProperty("handleOnClick")){
        onTabClick(tabData["handleOnClick"]());
    }
  };

  useEffect(() => {
    if(tabs.length > 0){  
        if(tabs[0].hasOwnProperty("handleOnClick")){
            onTabClick(tabs[0]["handleOnClick"]());
        }
    }
  }, [])

  return (
   <>
<div className="sm:hidden">
    <label htmlFor="tabs" className="sr-only">Select your country</label>
    <select options={tabs} id="tabs" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
    </select>
</div>
<ul className="hidden text-sm font-medium text-center text-gray-500 divide-x divide-gray-200 rounded-lg shadow sm:flex">
    {tabs.map((tab, index) => {
        return(
        <li className="w-full" key={tab.label + "_" + index}>
            <span className={`inline-block cursor-pointer w-full cursor-pointer p-4 ${activeTab === index ? "text-gray-900 bg-gray-100 rounded-l-lg" : "bg-white hover:text-gray-700 hover:bg-gray-50"} `} onClick={activeTab === index ? null : () => {handleTabClick(tab, index)}} >{tab.label}</span>
        </li>
        )
    })}
    {/* <li className="w-full">
        <span href="#" className="inline-block cursor-pointer w-full p-4 text-gray-900 bg-gray-100 rounded-l-lg" aria-current="page">Profile</span>
    </li>
    <li className="w-full">
        <span href="#" className="inline-block cursor-pointer w-full p-4 bg-white hover:text-gray-700 hover:bg-gray-50">Dashboard</span>
    </li>
    <li className="w-full">
        <span href="#" className="inline-block cursor-pointer w-full p-4 bg-white hover:text-gray-700 hover:bg-gray-50">Settings</span>
    </li>
    <li className="w-full">
        <span href="#" className="inline-block cursor-pointer w-full p-4 bg-white rounded-r-lg hover:text-gray-700 hover:bg-gray-50">Invoice</span>
    </li> */}
</ul>
</>
  );
}

export default TabBar;
