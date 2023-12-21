import axios from "axios";
import { getAccessToken, logOutUser } from "./SessionDetails";
import APIResponse, { createAPIResponse } from "../utils/APIResponse";
//import { getUserId, getIsUserLoggedIn } from "./session";

const IS_PRODUCTION_BUILD = false;

let domain = "http://localhost:5000";
if (IS_PRODUCTION_BUILD) {
  domain = import.meta.env.VITE_BACKEND_SERVER_URL;
}

const axiosInstance = axios.create({
  baseURL: domain
});

axiosInstance.interceptors.request.use(function (config) {
  const token = getAccessToken();
  if(token !== null && token !== undefined){
    config.headers.Authorization =  "Bearer " + token;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  response => {
    let apiResponse = createAPIResponse();
    apiResponse.statusCode = response.status;
    apiResponse.isSuccess = true;
    
    if(response.status === 204){
      apiResponse.successResponse.data.data = [];
    }else{
      if(response.hasOwnProperty("data")){
        apiResponse.successResponse.data = response.data;
      }  
    }
 
    return apiResponse;
  },
  error => {
    let apiResponse = createAPIResponse();
    let response = error.response;
    apiResponse.statusCode = response.status;
    apiResponse.isSuccess = false;

    if(response.data.hasOwnProperty("path")){
      if(response.data.message && isJSONString(response.data.message)){
        let data = JSON.parse(response.data.message);
        if(data.hasOwnProperty("error_code")){
          apiResponse.failureResponse.error_code = data.error_code;
        }
    
        if(data.hasOwnProperty("errors")){
          apiResponse.failureResponse.errors = data.errors;
        }

    
        if(data.hasOwnProperty("message")){
          apiResponse.failureResponse.message = data.message;
        }
      }else{
        if(response.data.hasOwnProperty("error_code")){
          apiResponse.failureResponse.error_code = response.data.error_code;
        }
    
        if(response.data.hasOwnProperty("errors")){
          apiResponse.failureResponse.errors = response.data.errors;
        }

        if(response.data.hasOwnProperty("error")){
          apiResponse.failureResponse.errors = response.data.error;
        }
        
        if(response.data.hasOwnProperty("message")){
          apiResponse.failureResponse.message = response.data.message;
        }
      }
    }else if(response.hasOwnProperty("data")){
      let data = response.data;
      if(data.hasOwnProperty("error_code")){
        apiResponse.failureResponse.error_code = data.error_code;
      }
  
      if(data.hasOwnProperty("errors")){
        apiResponse.failureResponse.errors = data.errors;
      }
  
      if(data.hasOwnProperty("message")){
        apiResponse.failureResponse.message = data.message;
      }
    }
    if(apiResponse.statusCode === 401 && (apiResponse.failureResponse.error_code !== "INVALID_CREDENTIALS")){
      logOutUser();
      window.location.href = "/home";
    }
    return apiResponse;
  }
);

function isJSONString(str) {
  try {
    JSON.parse(str);
    return true;
  } catch (error) {
    return false;
  }
}


export const getTopProducts = async () => {
    return await axiosInstance.get(domain + "/top-products").then(response => {
        return response;
      })
}

export const clearAndAddTopProductsInBulk = async (products) => {
  return await axiosInstance.post(domain + "/top-products", products).then(response => {
      return response;
    })
}


export const getTopCategories = async () => {
    return await axiosInstance.get(domain + "/top-categories").then(response => {
        return response;
      })
}

export const getTopCategory = async (category_id) => {
  return await axiosInstance.get(domain + `/top-category/${category_id}`).then(response => {
      return response;
    })
}

export const addTopCategory = async (animal_id, category_id, products) => {
  let body = {};
  body.animal_id = animal_id;
  body.category_id = category_id;
  body.products = products;
  return await axiosInstance.post(domain + "/top-category", body).then(response => {
      return response;
    })
}

export const deleteTopCategory = async (category_id) => {
  return await axiosInstance.delete(domain + "/top-category/" +category_id).then(response => {
      return response;
    })
}

export const getAllAnimals = async () => {
  return await axiosInstance.get(domain + "/animals/all").then(response => {
      return response;
    })
}

export const getAnimals = async (page, per_page) => {
  return await axiosInstance.get(domain + "/animals", {params: {page: page, per_page: per_page}}).then(response => {
      return response;
    })
}

export const getAnimal = async (animal_id) => {
  return await axiosInstance.get(domain + `/animal/${animal_id}`).then(response => {
      return response;
    })
}

export const createAnimal = async(name, description) => {
  let body = {};
  body.name = name;
  body.description = description;
  return await axiosInstance.post(domain + "/animal", body).then(response => {
    return response;
  })
}

export const updateAnimal = async(name, description, animal_id) => {
  let body = {};
  body.name = name;
  body.description = description;
  return await axiosInstance.put(domain + `/animal/${animal_id}`, body).then(response => {
    return response;
  })
}

export const addImageFileToAnimal = async(animal_id, file) => {
  const formData = new FormData();
  formData.append('animalImage', file);
  formData.append('image_type', "file");
  return await axiosInstance.put(domain + `/animal/${animal_id}/image`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }).then(response => {
    return response;
  })
}

export const addImageUrlToAnimal = async(animal_id, url) => {
  let body = {};
  body.url = url;
  return await axiosInstance.put(domain + `/animal/${animal_id}/image`, body, {params: {image_type: "external_url"}}).then(response => {
    return response;
  })
}

export const deleteImageFromAnimal = async(animal_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}/image`).then(response => {
    return response;
  })
}

export const deleteAnimal = async(animal_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}`).then(response => {
    return response;
  })
}

export const getCategory = async (animal_id, page, per_page) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/categories", {params: {page: page, per_page: per_page}}).then(response => {
      return response;
    })
}

export const getAllCategories = async (animal_id) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/categories/all").then(response => {
      return response;
    })
}

export const getCategoryBasedOnId = async (animal_id, category_id) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/category/" + category_id).then(response => {
      return response;
    })
}

export const createCategory = async(animal_id, name, description) => {
  let body = {};
  body.name = name;
  body.description = description;
  return await axiosInstance.post(domain + "/animal/" + animal_id + "/category", body).then(response => {
    return response;
  })
}

export const updateCategory = async(name, description, animal_id, category_id) => {
  let body = {};
  body.name = name;
  body.description = description;
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}`, body).then(response => {
    return response;
  })
}

export const addImageFileToCategory = async(animal_id, category_id, file) => {
  const formData = new FormData();
  formData.append('animalImage', file);
  formData.append('image_type', "file");
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}/image`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }).then(response => {
    return response;
  })
}

export const addImageUrlToCategory = async(animal_id, category_id, url) => {
  let body = {};
  body.url = url;
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}/image`, body, {params: {image_type: "external_url"}}).then(response => {
    return response;
  })
}

export const deleteImageFromCategory = async(animal_id, category_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}/category/${category_id}/image`).then(response => {
    return response;
  })
}

export const deleteCategory = async(animal_id, category_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}/category/${category_id}`).then(response => {
    return response;
  })
}

export const getAllAvailableProductsForVariation = async(animal_id, category_id, variant_type) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/category/" + category_id + "/product/variation/list",{params:{variant_type: variant_type}}).then(response => {
    return response;
  })
}

export const getProductVariation = async(animal_id, category_id, product_id) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/category/" + category_id + "/product/" + product_id + "/variation").then(response => {
    return response;
  })
}

export const createProductVariation = async(animal_id, category_id, product_id, variant_animal_id, variant_category_id, variant_product_id, variant_type) => {
  let body = {};
  body.animal_id = variant_animal_id;
  body.category_id = variant_category_id;
  body.product_id = variant_product_id;
  body.variant_type = variant_type;
  return await axiosInstance.post(domain + "/animal/" + animal_id + "/category/" + category_id + "/product/" + product_id + "/variation", body).then(response => {
    return response;
  })
}

export const deleteProductVariation = async(animal_id, category_id, product_id, variant_type) => {
  let body = {};
  body.variant_type = variant_type;
  return await axiosInstance.delete(domain + "/animal/" + animal_id + "/category/" + category_id + "/product/" + product_id + "/variation", { data: body }).then(response => {
    return response;
  })
}

export const getProduct = async(animal_id, category_id, product_id) => {
  return await axiosInstance.get(domain + "/animal/" + animal_id + "/category/" + category_id + "/product/" + product_id).then(response => {
    return response;
  })
}

export const getProducts = async(animal_id, category_id, page, per_page, columnName, isAsc, minPrice, maxPrice) => {
  let sort_order;
  if(isAsc !== undefined){
    sort_order = isAsc ? "asc" : "desc";
  }

  let sort_by;
  if(columnName !== undefined){
    sort_by = columnName;
  }

  let price_min, price_max;
  if(minPrice){
    price_min = minPrice;
  }
  if(maxPrice){
    price_max = maxPrice;
  }

  return await axiosInstance.get(domain + "/animal/" + animal_id + "/category/" + category_id + "/products", {params: {page: page, per_page: per_page, sort_order: sort_order ? sort_order : undefined, sort_by: sort_by ? sort_by : undefined, price_min: price_min ? price_min : undefined, price_max: price_max ? price_max : undefined}}).then(response => {
    return response;
  })
}

export const createProduct = async(animal_id, category_id, productInfo) => {
  let body = {};
  body.name = productInfo.name;
  body.description = productInfo.description;
  body.details = productInfo.details;
  body.stocks = productInfo.stocks;
  body.price = productInfo.price;
  if(productInfo.hasOwnProperty("color")){
    body.color = productInfo.color;
  }
  if(productInfo.hasOwnProperty("size")){
    body.size = productInfo.size;
  }
  if(productInfo.hasOwnProperty("weightUnits") && productInfo.hasOwnProperty("weight")){
    body.weightUnits = productInfo.weightUnits;
    body.weight = productInfo.weight;
  }
  return await axiosInstance.post(domain + `/animal/${animal_id}/category/${category_id}/product`, body).then(response => {
    return response;
  })
}

export const updateProduct = async(animal_id, category_id, product_id, productInfo) => {
  let body = {};
  if(productInfo.hasOwnProperty("name")){
    body.name = productInfo.name;
  }
  if(productInfo.hasOwnProperty("description")){
    body.description = productInfo.description;
  }
  if(productInfo.hasOwnProperty("details")){
    body.details = productInfo.details;
  }
  if(productInfo.hasOwnProperty("stocks")){
    body.stocks = productInfo.stocks;
  }
  if(productInfo.hasOwnProperty("price")){
    body.price = productInfo.price;
  }
  if(productInfo.hasOwnProperty("color")){
    body.color = productInfo.color;
  }
  if(productInfo.hasOwnProperty("size")){
    body.size = productInfo.size;
  }
  if(productInfo.hasOwnProperty("weightUnits") && productInfo.hasOwnProperty("weight")){
    body.weightUnits = productInfo.weightUnits;
    body.weight = productInfo.weight;
  }
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}`, body).then(response => {
    return response;
  })
}

export const addImageFileToProduct = async(animal_id, category_id, product_id, file) => {
  const formData = new FormData();
  formData.append('productImage', file);
  formData.append('image_type', "file");
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}/image`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }).then(response => {
    return response;
  })
}

export const addImageUrlsToProduct = async(animal_id, category_id, product_id, urls) => {
  let imageUrl = [];
  for(let i = 0; i < urls.length; i++){
    imageUrl.push({url: urls[i]});
  }
  return await axiosInstance.put(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}/image`, imageUrl, {params: {image_type: "external_url"}}).then(response => {
    return response;
  })
}

export const deleteImagesFromProduct = async(animal_id, category_id, product_id, image_ids) => {
  let body = {};
  body.image_ids = image_ids;
  return await axiosInstance.delete(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}/images`, { data: body }).then(response => {
    return response;
  })
}

export const deleteImageFromProduct = async(animal_id, category_id, product_id, image_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}/image/${image_id}`).then(response => {
    return response;
  })
}

export const deleteProduct = async(animal_id, category_id, product_id) => {
  return await axiosInstance.delete(domain + `/animal/${animal_id}/category/${category_id}/product/${product_id}`).then(response => {
    return response;
  })
}

export const activateAdminUser = async(email) => {
  return await axiosInstance.put(domain + "/user/activate", {}, {params: {email: email}}).then(response => {
    return response;
  })
}

export const confirmUser = async(userId, otpValue) => {
  let body = {};
  body.user_id = userId;
  body.confirmation_code = otpValue;
  return await axiosInstance.post(domain + "/user/confirm-user", body).then(response => {
      return response;
    })

}

export const registerUser = async (name, email, password, phoneNumber) => {
    let body = {};
    body.name = name;
    body.email = email;
    body.password = password;
    body.phone_number = phoneNumber;
    return await axiosInstance.post(domain + "/user/register", body).then(response => {
        return response;
      })
}

export const authenticateUser = async (email, password) => {
  let body = {};
  body.email = email;
  body.password = password;
  return await axiosInstance.post(domain + "/user/authenticate", body).then(response => {
      return response;
    })

}

export const signOutUser = async () => {
  return await axiosInstance.post(domain + "/user/signout").then(response => {
      return response;
    })

}

export const forgotPasswordOTPTrigger = async (email) => {
  let body = {};
  body.email = email;
  return await axiosInstance.post(domain + "/user/trigger-change-password-otp", body).then(response => {
      return response;
    })

}

export const validateAndUpdateForgotPassword = async (email, otp, password) => {
  let body = {};
  body.email = email;
  body.new_password = password;
  body.forgot_password_otp = otp;
  return await axiosInstance.post(domain + "/user/forgot-password", body).then(response => {
      return response;
    })
}

export const changePassword = async (oldPassword, newPassword) => {
  let body = {};
  body.old_password = oldPassword;
  body.new_password = newPassword;
  return await axiosInstance.put(domain + "/user", body).then(response => {
      return response;
    })
}

export const getAllAddress = async() => {
  return await axiosInstance.get(domain + "/user/address").then(response => {
      return response;
    })
}

export const saveAddress = async(address, city, state, country, pincode) => {
  let body = {};
  body.address = address;
  body.city = city;
  body.state = state;
  body.country = country;
  body.pincode = pincode;
  return await axiosInstance.post(domain + "/user/address",body).then(response => {
      return response;
    })
}

export const updateAddress = async(addressId, address, city, state, country, pincode) => {
  let body = {};
  body.address = address;
  body.city = city;
  body.state = state;
  body.country = country;
  body.pincode = pincode;
  return await axiosInstance.put(domain + "/user/address/" + addressId,body).then(response => {
      return response;
    })
}

export const deleteAddress = async(addressId) => {
  return await axiosInstance.delete(domain + "/user/address/" + addressId).then(response => {
      return response;
    })
}
export const getAddress = async(address_id) => {
  return await axiosInstance.get(domain + "/user/address/" + address_id).then(response => {
      return response;
    })
}

export const selectDefaultAddress = async(address_id) => {
  return await axiosInstance.put(domain + "/user/address/" + address_id + "/default").then(response => {
      return response;
    })
}

export const getPromotions = async() => {
  return await axiosInstance.get(domain + "/promotions").then(response => {
    return response;
  })
}

export const addPromotions = async(file) => {
  const formData = new FormData();
  formData.append('promotionImage', file);
  return await axiosInstance.post(domain + "/promotions", formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }).then(response => {
    return response;
  })
}

export const deletePromotion = async(id) => {
  return await axiosInstance.delete(domain + "/promotion/"+id).then(response => {
    return response;
  })
}

