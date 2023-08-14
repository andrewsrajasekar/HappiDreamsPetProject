import { useEffect, useState } from "react";
import { ChevronDownIcon, ChevronRightIcon } from '@heroicons/react/20/solid'

function Sidebar({tabs, activeTabLabel}) {
    const [activeTab, setActiveTab] = useState({label: ""});

    const handleTabPress = (tabFunction, index, tabDetails) => {
      setActiveTab(tabDetails.label);
      if(tabFunction !== undefined){
        tabFunction();
      }
    };


    const [heightOfSideBar, setHeightOfSideBar] = useState();
    useEffect(() => {
        const calculateRemainingHeight = () => {
            const navbarHeight = document.getElementById("navBar").offsetHeight;
            const footerHeight = document.getElementById("footer").offsetHeight;
            const windowHeight = window.innerHeight;
            const remainingHeight = windowHeight - navbarHeight - footerHeight;
            setHeightOfSideBar(remainingHeight);
        };

        calculateRemainingHeight();
        window.addEventListener('resize', calculateRemainingHeight);

        return () => {
            window.removeEventListener('resize', calculateRemainingHeight);
        };
    }, [])


    const renderChildMenuRecursively = (tabsData, parentIndex) => {
        const [isChildMenuOpened, setIsChildMenuOpened] = useState([]);
  
        const handleChildMenuOpen = (index) => {
            const updatedStates = [...isChildMenuOpened];
            updatedStates[index] = !updatedStates[index];
            setIsChildMenuOpened(updatedStates);
        };
        
        return tabsData.map((tab, index) => {
            let isChildMenu = tab.hasOwnProperty("childMenu");
            let currentIndex = parentIndex.toString().trim().length > 0 ?  parentIndex + "_" + index : index;
            currentIndex = currentIndex.toString();
            let isActive = activeTab === tab.label || activeTabLabel === tab.label;
       return( 
        <span key={currentIndex}>
       <span id={tab.label + "_" + index}  className={`font-medium text-sm items-center rounded-lg text-gray-900 px-4 py-2.5 flex transition-all duration-200 hover:bg-gray-200 group ${(tab.hasOwnProperty("handleTabChange") && typeof tab.handleTabChange === 'function') || isChildMenu ? "cursor-pointer" : ""} ${isActive && !isChildMenu ? "bg-gray-200" : ""}`} onClick={() => {tab.hasOwnProperty("handleTabChange") && typeof tab.handleTabChange === 'function' ? handleTabPress( () => tab.handleTabChange(tab, index), currentIndex, tab ) : isChildMenu ? handleChildMenuOpen(index) : handleTabPress(null, currentIndex, tab)}}>
            <div className="">{tab.label}</div>
          <ChevronRightIcon className={`w-5 h-5 mt-[4.3px] transition-transform ${isChildMenu && !isChildMenuOpened[index] ? "" : "hidden"}`} />
            <ChevronDownIcon className={`w-5 h-5 mt-[4.3px] transition-transform ${isChildMenu && isChildMenuOpened[index] ? "" : "hidden"}`} />
          </span>
          
          {isChildMenu && <div id={currentIndex} className={`pl-4 ${isChildMenuOpened[index] ? "" : "hidden"}`}>
          {renderChildMenuRecursively(tab.childMenu, currentIndex)}
    </div>}
    </span>

    ) 
        })
    }

    
    const renderedTabs =  renderChildMenuRecursively(tabs, "");


    return (
        <div className="bg-white lg:flex md:w-auto md:flex-col">
            <div className="flex-col pt-5 flex bg-gray-100 overflow-y-auto  flex-grow" style={{ height: heightOfSideBar }}>
                <div className="flex-col justify-between px-4 flex">
                    <div className="space-y-4">
                        <div className="bg-top bg-cover space-y-1">
                            {renderedTabs}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Sidebar;