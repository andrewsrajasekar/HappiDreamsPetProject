import Footer from './components/Footer.jsx';
import HomeCategorySampleProducts from './pages/HomeCategorySampleProducts.jsx';
import HomeTopProducts from './pages/HomeTopProducts.jsx';
import NavBar from './components/NavBar.jsx';
import './index.css';
import LoginPage from './pages/LoginPage.jsx';
import SignUpPage from './pages/SignUpPage.jsx';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate
} from "react-router-dom";
import Category from './pages/Category.jsx';
import Products from './pages/Products.jsx';
import { CATEGORY_TYPE } from "./utils/CategoryTypes";
import AboutUs from './pages/AboutUs.jsx';
import Product from './pages/Product.jsx';
import Cart from './pages/Cart.jsx';
import ForgotPassword from './pages/ForgotPassword.jsx';
import 'react-toastify/dist/ReactToastify.css';
import 'babel-polyfill';
import { ToastContainer  } from 'react-toastify';
import AccountsPage from './pages/AccountsPage.jsx';
import MainPageCarousel from './components/MainPageCarousel.jsx';
import OrderHistoryPage from './pages/OrderHistoryPage.jsx';
import OrderSummary from './pages/OrdersSummary.jsx';
import AdminPanel from './pages/AdminPanel.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';
import { useEffect, useState } from 'react';
import { getTopCategories } from './services/ApiClient.jsx';
import { isUserLoggedIn } from './services/SessionDetails.jsx';
import { addKeyInActions, getKeyInActions } from './services/AfterReloadActions.jsx';
import UINotification from './components/UINotification.jsx';

function App() {
  window.onbeforeunload = function () {
    window.scrollTo(0, 0);
  }

  useEffect(() => {
    let isSuccessAfterLogin = getKeyInActions("showSuccessAfterLogin");
    let isSuccessAfterLogout = getKeyInActions("showSuccessAfterLogOut");
    isSuccessAfterLogin = isSuccessAfterLogin != null ? isSuccessAfterLogin : false;
    isSuccessAfterLogout = isSuccessAfterLogout != null ? isSuccessAfterLogout : false;
    if (isSuccessAfterLogin) {
      addKeyInActions("showSuccessAfterLogin", false);
      UINotification({ message: "User Logged In Successfully", type: "Success" });
    }
    if(isSuccessAfterLogout){
      addKeyInActions("showSuccessAfterLogOut", false);
      UINotification({ message: "User Logged Out Successfully", type: "Success" });
    }
  }, []);

  const [categorySampleProducts, setCategorySampleProducts] = useState([]);
  const [isLogInDone, setIsLogInDone] = useState(isUserLoggedIn());


  useEffect(() => {
    const fetchData = async () => {
      const response = await getTopCategories();
        if(response.isSuccess && Array.isArray(response.successResponse.data) && response.successResponse.data.length > 0){
          setCategorySampleProducts(response.successResponse.data);
        }else{
          setCategorySampleProducts([]);
        }
    };

    fetchData();
    
  }, [])

  // const categorySampleProducts = [
  //   {
  //     "categoryName": "Dummy Category 1", "animalType": "Dog", "products":
  //       [
  //         {
  //           "name": "Dummy Product 1_1",
  //           "description": "Dummy Description 1_1",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 1_2",
  //           "description": "Dummy Description 1_2",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 1_3",
  //           "description": "Dummy Description 1_3",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         }
  //         ,
  //         {
  //           "name": "Dummy Product 1_4",
  //           "description": "Dummy Description 1_4",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         }
  //       ]
  //   },
  //   {
  //     "categoryName": "Dummy Category 2", "animalType": "Cat", "products":
  //       [
  //         {
  //           "name": "Dummy Product 2_1",
  //           "description": "Dummy Description 2_1",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 2_2",
  //           "description": "Dummy Description 2_2",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 2_3",
  //           "description": "Dummy Description 2_3",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         }
  //         ,
  //         {
  //           "name": "Dummy Product 2_4",
  //           "description": "Dummy Description 2_4",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         }
  //       ]
  //   },
  //   {
  //     "categoryName": "Dummy Category 3", "animalType": "Fish", "products":
  //       [
  //         {
  //           "name": "Dummy Product 3_1",
  //           "description": "Dummy Description 3_1",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 3_2",
  //           "description": "Dummy Description 3_2",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         },
  //         {
  //           "name": "Dummy Product 3_3",
  //           "description": "Dummy Description 3_3",
  //           "image_url": "https://dummyimage.com/1203x503"
  //         }
  //       ]
  //   }
  // ]

  return (
    <div className='flex flex-col min-h-screen'>
<ToastContainer />
      <Router>
        <NavBar />
        <Routes className='flex-grow'>

          <Route
            path="/login"
            element={
              <>
              {!isLogInDone ? <LoginPage /> : <Navigate to="/home" />}
              </>
            }
          />
          <Route
            path="/signup"
            element={
              <>
              {!isLogInDone ? <SignUpPage /> : <Navigate to="/home" />}
              </>
            }
          />
          <Route
            path="/"
            element={
              <div>
                <MainPageCarousel isPopUp={false} />
                <HomeTopProducts />
                <HomeCategorySampleProducts categorySampleProducts={categorySampleProducts} />
              </div>
            }
          />
            <Route
            path="/home"
            element={
              <div>
                <MainPageCarousel />
                <HomeTopProducts />
                <HomeCategorySampleProducts categorySampleProducts={categorySampleProducts} />
              </div>
            }
          />

          <Route
            path="/animals"
            element={
              <Category key={CATEGORY_TYPE.ANIMAL_CATEGORY} categoryType={CATEGORY_TYPE.ANIMAL_CATEGORY} />
            }
          />

          <Route
            path="/:animal_id/categories"
            element={
              <Category key={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} categoryType={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} />
            }
          />

          <Route
            path="/:animal_id/:category_id/products"
            element={
              <Products />
            }
          />


          <Route
            path="/:animal_type/:category_name/:product_id"
            element={
              <Product />
            }
          />

          <Route
            path="/aboutus"
            element={
              <AboutUs />
            }
          />
          <Route
            path="/cart"
            element={
              <>
              {isLogInDone ? <Cart /> : <Navigate to="/home" />}
              </>
            }
          />
          <Route
            path="/forgotpassword"
            element={
              <>
              {!isLogInDone ? <ForgotPassword /> : <Navigate to="/home" />}
              </>
            }
          />
          <Route
          path="/accounts"
          element={
            <>
              {isLogInDone ? <AccountsPage /> : <Navigate to="/home" />}
            </>
          }
          />
          <Route
          path="/accounts/password"
          element={
            <>
              {isLogInDone ? <AccountsPage selectedTab="Password" /> : <Navigate to="/home" />}
            </>
          }
          />
          <Route
          path="/accounts/address"
          element={
            <>
              {isLogInDone ? <AccountsPage selectedTab="Address" /> : <Navigate to="/home" />}
            </>
          }
          />
          <Route
          path="/orderhistory"
          element={
            <>
              {isLogInDone ? <OrderHistoryPage /> : <Navigate to="/home" />}
            </>
          }
          />
          <Route
          path="/orderhistory/:order_number"
          element={
            <>
              {isLogInDone ? <OrderSummary /> : <Navigate to="/home" />}
            </>
          }
          />
          <Route
          path="/adminpanel"
          element={
            <>
            {isLogInDone ? <AdminPanel /> : <Navigate to="/home" />}
            </>
          }
          />

        <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Router>
      <Footer />
    </div>

  );
}

export default App
