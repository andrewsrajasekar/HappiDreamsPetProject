export const logInUser = (userId, name, email, role, roleId, access_token, expiration_date) => {
    let userData = JSON.parse(localStorage.getItem("user_data"));
    userData = userData === null || userData === undefined ? {} : userData;
    userData.name = name;
    userData.id = userId;
    userData.email = email;
    userData.role= role;
    userData.roleId = roleId;
    userData.access_token = access_token;
    userData.expiration_date = expiration_date;
    localStorage.setItem("user_data", JSON.stringify(userData));
}

export const isUserLoggedIn = () => {
    let userData = JSON.parse(localStorage.getItem("user_data"));
    userData = userData === null || userData === undefined ? {} : userData;
    if(userData.hasOwnProperty("name") && userData.hasOwnProperty("id") && userData.hasOwnProperty("email") && userData.hasOwnProperty("role") && userData.hasOwnProperty("roleId") && userData.hasOwnProperty("access_token") && userData.hasOwnProperty("expiration_date")){
        return true;
    }
    return false;
}

export const logOutUser = () => {
    localStorage.removeItem("user_data");
}

export const getAccessToken = () => {
    let userData = !(localStorage.getItem("user_data") === undefined || localStorage.getItem("user_data")  === null) ? JSON.parse(localStorage.getItem("user_data")) : {};
    if(userData.hasOwnProperty("access_token")){
        return userData.access_token;
    }
    return null;
}

export const getUserName = () => {
    let userData = !(localStorage.getItem("user_data") === undefined || localStorage.getItem("user_data")  === null) ? JSON.parse(localStorage.getItem("user_data")) : {};
    if(userData.hasOwnProperty("access_token")){
        return userData.name;
    }
    return null;
}

export const isAdmin = () => {
    let userData = !(localStorage.getItem("user_data") === undefined || localStorage.getItem("user_data")  === null) ? JSON.parse(localStorage.getItem("user_data")) : {};
    if(userData.hasOwnProperty("role") && userData.hasOwnProperty("roleId")){
        if(userData.role.toLowerCase() === "admin" && userData.roleId === 1){
            return true;
        }
    }
    return false;
}

export const isUser = () => {
    let userData = !(localStorage.getItem("user_data") === undefined || localStorage.getItem("user_data")  === null) ? JSON.parse(localStorage.getItem("user_data")) : {};
    if(userData.hasOwnProperty("role") && userData.hasOwnProperty("roleId")){
        if(userData.role.toLowerCase() === "user" && userData.roleId === 2){
            return true;
        }
    }
    return false;
}