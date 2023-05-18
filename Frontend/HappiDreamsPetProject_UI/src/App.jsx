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
  Navigate,
} from "react-router-dom";
import Category from './pages/Category.jsx';
import Products from './pages/Products.jsx';
import { CATEGORY_TYPE } from "./utils/CategoryTypes";
import AboutUs from './pages/AboutUs.jsx';
import Product from './pages/Product.jsx';
import Cart from './pages/Cart.jsx';
import ForgotPassword from './pages/ForgotPassword.jsx';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer  } from 'react-toastify';
import AccountsPage from './pages/AccountsPage.jsx';
import MainPageCarousel from './components/MainPageCarousel.jsx';
import OrderHistoryPage from './pages/OrderHistoryPage.jsx';

function App() {
  window.onbeforeunload = function () {
    window.scrollTo(0, 0);
  }

  const categorySampleProducts = [
    {
      "categoryName": "Dummy Category 1", "animalType": "Dog", "products":
        [
          {
            "name": "Dummy Product 1_1",
            "description": "Dummy Description 1_1",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 1_2",
            "description": "Dummy Description 1_2",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 1_3",
            "description": "Dummy Description 1_3",
            "image_url": "https://dummyimage.com/1203x503"
          }
          ,
          {
            "name": "Dummy Product 1_4",
            "description": "Dummy Description 1_4",
            "image_url": "https://dummyimage.com/1203x503"
          }
        ]
    },
    {
      "categoryName": "Dummy Category 2", "animalType": "Cat", "products":
        [
          {
            "name": "Dummy Product 2_1",
            "description": "Dummy Description 2_1",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 2_2",
            "description": "Dummy Description 2_2",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 2_3",
            "description": "Dummy Description 2_3",
            "image_url": "https://dummyimage.com/1203x503"
          }
          ,
          {
            "name": "Dummy Product 2_4",
            "description": "Dummy Description 2_4",
            "image_url": "https://dummyimage.com/1203x503"
          }
        ]
    },
    {
      "categoryName": "Dummy Category 3", "animalType": "Fish", "products":
        [
          {
            "name": "Dummy Product 3_1",
            "description": "Dummy Description 3_1",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 3_2",
            "description": "Dummy Description 3_2",
            "image_url": "https://dummyimage.com/1203x503"
          },
          {
            "name": "Dummy Product 3_3",
            "description": "Dummy Description 3_3",
            "image_url": "https://dummyimage.com/1203x503"
          }
        ]
    }
  ]

  return (
    <div className='flex flex-col min-h-screen'>
<ToastContainer />
      <Router>
        <NavBar />
        <Routes className='flex-grow'>

          <Route
            path="/login"
            element={
              <LoginPage />
            }
          />
          <Route
            path="/signup"
            element={
              <SignUpPage />
            }
          />
          <Route
            path="/"
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
            path="/:animal_type/categories"
            element={
              <Category key={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} categoryType={CATEGORY_TYPE.ANIMAL_PRODUCT_CATEGORY} />
            }
          />

          <Route
            path="/:animal_type/:category_name/products"
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
              <Cart />
            }
          />
          <Route
            path="/forgotpassword"
            element={
              <ForgotPassword />
            }
          />
          <Route
          path="/accounts"
          element={
            <AccountsPage />
          }
          />
          <Route
          path="/orderhistory"
          element={
            <OrderHistoryPage />
          }
          />
        </Routes>
      </Router>
      <Footer />
    </div>

  );
}

export default App
