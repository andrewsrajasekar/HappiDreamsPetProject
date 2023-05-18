import { useRef, useEffect, useState } from 'react';
import { UserIcon, TruckIcon, ShoppingCartIcon } from '@heroicons/react/24/outline';
import { useNavigate } from 'react-router-dom';


function ProfileDropdown(){
    const [isOpen, setIsOpen] = useState(false);
    const ref = useRef(null);
    const navigate = useNavigate();
    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
      const goToAccountsPage = () => {
        navigate("/accounts");
      }

      
    useEffect(() => {
        const handleClickOutside = (event) => {
        if (ref.current && !ref.current.contains(event.target)) {
            setIsOpen(false);
        }
        };

        document.addEventListener('click', handleClickOutside);

        return () => {
        document.removeEventListener('click', handleClickOutside);
        };
    }, [ref]);
    
    const goToCartPage = () => {
        navigate("/cart");
      }
    

    return (
        <> 
              <div ref={ref} onClick={()=>{setIsOpen(!isOpen)}} className={`border-transparent ${isOpen ? "border-purple-700 transform transition duration-300" : ""}`} x-transitionenter-end="transform opacity-100 scale-100" x-transitionleave="transition ease-in duration-75" x-transitionleave-start="transform opacity-100 scale-100">
                <div className="flex justify-center items-center cursor-pointer">
                  {/* <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-gray-900">
                    <img src="https://images.unsplash.com/photo-1610397095767-84a5b4736cbd?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80" alt="" className="w-full h-full object-cover" />
                  </div> */}
                  <div className={`${isMobileView() ? "text-gray-700" : "text-gray-100"} hover:text-teal-400 font-medium`}>
                    <div className="cursor-pointer">Hello! Andrews Rajasekar</div>
                  </div>
                </div>
                { isOpen && <div x-show={isOpen.toString()} x-transitionenter="transition ease-out duration-100" x-transitionenter-start="transform opacity-0 scale-95" x-transitionenter-end="transform opacity-100 scale-100" x-transitionleave="transition ease-in duration-75" x-transitionleave-start="transform opacity-100 scale-100" x-transitionleave-end="transform opacity-0 scale-95" className="absolute w-60 px-5 py-3 bg-white rounded-lg shadow border mt-5">
                  <ul className="space-y-3">
                    <li className="font-medium">
                      <span onClick={goToAccountsPage} className="cursor-pointer flex items-center transform transition-colors duration-200 border-r-4 border-transparent hover:border-indigo-700 hover:bg-gray-100">
                        <div className='mr-3'>
                        <UserIcon className='w-6 h-6'/>
                        </div>
                        Account
                      </span>
                    </li>
                    <li className="font-medium"> 
                      <span className="cursor-pointer flex items-center transform transition-colors duration-200 border-r-4 border-transparent hover:border-indigo-700 hover:bg-gray-100">
                      <div className='mr-3'>
                      <TruckIcon className="w-6 h-6" />
                      </div>
                        Order History
                      </span>
                    </li>
                    <li className="font-medium"> 
                      <span onClick={goToCartPage} className="cursor-pointer flex items-center transform transition-colors duration-200 border-r-4 border-transparent hover:border-indigo-700 hover:bg-gray-100">
                      <div className='mr-3'>
                      <ShoppingCartIcon className="w-6 h-6" />
                      </div>
                        Cart
                      </span>
                    </li>
                    <hr className="" />
                    <li className="font-medium">
                      <span className="cursor-pointer flex items-center transform transition-colors duration-200 border-r-4 border-transparent hover:border-red-600 hover:bg-red-100">
                        <div className="mr-3 text-red-600">
                          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
                        </div>
                        Logout
                      </span>
                    </li>
                  </ul>
                </div> }
              </div>
          </>
    );
}

export default ProfileDropdown;