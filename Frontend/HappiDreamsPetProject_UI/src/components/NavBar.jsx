import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import ProfileDropdown from "./ProfileDropdown";

function NavBar(){
        const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
        const companyName = "HappiDreamPets";
        const navigate = useNavigate();

        const goToLogin = () => {
          navigate("/login");
        }

        const goToHome = () => {
          navigate("/");
        }
        const goToAnimalTypePage = () => {
          navigate("/animals");
        }
        const goToAboutUs = () => {
          navigate("/aboutus");
        }

        return (
          <div className="bg-gray-900 sticky top-0 z-50" id="navBar">
            <div className="px-4 py-5 mx-auto sm:max-w-xl md:max-w-full lg:max-w-screen-xl md:px-24 lg:px-8">
              <div className="relative flex items-center justify-between">
                <span
                  href="/"
                  aria-label={companyName}
                  title={companyName}
                  onClick={goToHome}
                  className="inline-flex items-center cursor-pointer"
                >
                  <span className="ml-2 text-xl font-bold tracking-wide text-gray-100 uppercase">
                    {companyName}
                  </span>
                </span>
                <ul className="flex items-center hidden space-x-8 lg:flex">
                  <li>
                    <span
                      aria-label="AnimalTypes"
                      title="AnimalTypes"
                      className="font-medium tracking-wide text-gray-100 transition-colors duration-200 hover:text-teal-400 cursor-pointer"
                      onClick={goToAnimalTypePage}
                    >
                      Animal Types
                    </span>
                  </li>
                  <li>
                    <span
                      aria-label="AboutUs"
                      title="AboutUs"
                      className="font-medium tracking-wide text-gray-100 transition-colors duration-200 hover:text-teal-400 cursor-pointer"
                      onClick={goToAboutUs}
                    >
                      About Us
                    </span>
                  </li>
                  <li>
                    <span
                      className="inline-flex items-center justify-center h-12 px-6 font-medium tracking-wide text-white transition duration-200 rounded shadow-md bg-purple-600 hover:bg-purple-900 focus:shadow-outline focus:outline-none cursor-pointer"
                      aria-label="Login / Signup"
                      title="Login / Signup"
                      onClick={goToLogin}
                    >
                      Login / Signup
                    </span>
                  </li>
                  <li>
                    <ProfileDropdown />
                  </li>
                </ul>
                <div className="lg:hidden">
                  <button
                    aria-label="Open Menu"
                    title="Open Menu"
                    className="p-2 -mr-1 transition duration-200 rounded focus:outline-none focus:shadow-outline"
                    onClick={() => setIsMobileMenuOpen(true)}
                  >
                    <svg className="w-5 text-gray-600" viewBox="0 0 24 24">
                      <path
                        fill="currentColor"
                        d="M23,13H1c-0.6,0-1-0.4-1-1s0.4-1,1-1h22c0.6,0,1,0.4,1,1S23.6,13,23,13z"
                      />
                      <path
                        fill="currentColor"
                        d="M23,6H1C0.4,6,0,5.6,0,5s0.4-1,1-1h22c0.6,0,1,0.4,1,1S23.6,6,23,6z"
                      />
                      <path
                        fill="currentColor"
                        d="M23,20H1c-0.6,0-1-0.4-1-1s0.4-1,1-1h22c0.6,0,1,0.4,1,1S23.6,20,23,20z"
                      />
                    </svg>
                  </button>
                  {isMobileMenuOpen && (
                    <div className="absolute top-0 left-0 w-full">
                      <div className="p-5 bg-white border rounded shadow-sm">
                        <div className="flex items-center justify-between mb-4">
                          <div>
                            <button
                              aria-label="Close Menu"
                              title="Close Menu"
                              className="p-2 -mt-2 -mr-2 transition duration-200 rounded hover:bg-gray-200 focus:bg-gray-200 focus:outline-none focus:shadow-outline"
                              onClick={() => setIsMobileMenuOpen(false)}
                            >
                              <svg className="w-5 text-gray-600" viewBox="0 0 24 24">
                                <path
                                  fill="currentColor"
                                  d="M19.7,4.3c-0.4-0.4-1-0.4-1.4,0L12,10.6L5.7,4.3c-0.4-0.4-1-0.4-1.4,0s-0.4,1,0,1.4l6.3,6.3l-6.3,6.3 c-0.4,0.4-0.4,1,0,1.4C4.5,19.9,4.7,20,5,20s0.5-0.1,0.7-0.3l6.3-6.3l6.3,6.3c0.2,0.2,0.5,0.3,0.7,0.3s0.5-0.1,0.7-0.3 c0.4-0.4,0.4-1,0-1.4L13.4,12l6.3-6.3C20.1,5.3,20.1,4.7,19.7,4.3z"
                                />
                              </svg>
                            </button>
                          </div>
                        </div>
                        <nav>
                          <ul className="space-y-4">
                            <li>
                              <span
          
                                aria-label="AnimalTypes"
                                title="AnimalTypes"
                                className="font-medium tracking-wide text-gray-700 transition-colors duration-200 hover:text-teal-400"
                                onClick={goToAnimalTypePage}
                              >
                                Animal Types
                              </span>
                            </li>
                            <li>
                            <span
                              aria-label="AboutUs"
                              title="AboutUs"
                              className="font-medium tracking-wide text-gray-700 transition-colors duration-200 hover:text-teal-400"
                              onClick={goToAboutUs}
                            >
                              About Us
                            </span>
                          </li>
                            <li>
                              <span
                                className="inline-flex items-center justify-center w-full h-12 px-6 font-medium tracking-wide text-white transition duration-200 rounded shadow-md bg-purple-600 hover:bg-purple-900 focus:shadow-outline focus:outline-none"
                                aria-label="Login / Sign up"
                                title="Login / Sign up"
                                onClick={goToLogin}
                              >
                                Login / Sign up
                              </span>
                            </li>
                              <li>
                              <ProfileDropdown />
                            </li>
                          </ul>
                        </nav>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        );
}

export default NavBar;