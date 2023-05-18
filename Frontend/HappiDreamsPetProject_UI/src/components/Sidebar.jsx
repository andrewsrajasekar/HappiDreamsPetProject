import { useEffect, useState } from "react";

function Sidebar({handleTabChange}) {

    const [heightOfSideBar, setHeightOfSideBar] = useState();
    useEffect(() => {
        const calculateRemainingHeight = () => {
            const navbarHeight = document.getElementById("navBar").offsetHeight;
            const footerHeight = document.getElementById("footer").offsetHeight;
            const windowHeight = window.innerHeight;
            const remainingHeight = windowHeight - navbarHeight - footerHeight - 0.5 ;
            setHeightOfSideBar(remainingHeight);
        };

        calculateRemainingHeight();
        window.addEventListener('resize', calculateRemainingHeight);

        return () => {
            window.removeEventListener('resize', calculateRemainingHeight);
        };
    }, [])

    return (
        <div className="bg-white lg:flex md:w-64 md:flex-col">
            <div className="flex-col pt-5 flex bg-gray-100 overflow-y-auto" style={{ height: heightOfSideBar }}>
                <div className="flex-col justify-between px-4 flex">
                    <div className="space-y-4">
                        <div className="bg-top bg-cover space-y-1">
                            <span className="font-medium text-sm items-center rounded-lg text-gray-900 px-4 py-2.5 flex
                    transition-all duration-200 hover:bg-gray-200 group cursor-pointer" onClick={() => {handleTabChange("password")}}>Password</span>
                            <span className="font-medium text-sm items-center rounded-lg text-gray-900 px-4 py-2.5 flex
                    transition-all duration-200 hover:bg-gray-200 group cursor-pointer" onClick={() => {handleTabChange("address")}}>Address</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Sidebar;