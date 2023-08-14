import React, { useEffect, useState } from "react";
import Select from "react-select";

const AddressForm = ({ onSave, isEdit, editData, addressId }) => {
  const isEditMode = isEdit !== undefined ? isEdit : false;

  const [address, setAddress] = useState(isEditMode && editData.address ? editData.address : "");
  const [pinCode, setPinCode] = useState(isEditMode && editData.pinCode ? editData.pinCode : "");

  const [allStateAndCity, setAllStateAndCity] = useState();

  const [country, setCountry] = useState(isEditMode && editData.country ? editData.country : "");
  const [state, setState] = useState(isEditMode && editData.state ? editData.state  : "");
  const [city, setCity] = useState(isEditMode && editData.city ? editData.city  : "");

  // Fetch list of countries and states from API
  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  const[isCountryLoading, setIsCountryLoading] = useState(false);
  const[isStateLoading, setIsStateLoading] = useState(false);
  const[isCityLoading, setIsCityLoading] = useState(false);

  const [isSubmitButtonDisabled, setIsSubmitButtonDisabled] = useState(false);
  const [isFormValid, setIsFormValid] = useState(false);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const setStateAndCity = async () => {
      const response = await fetch('../../assets/India.json');
      const jsonData = await response.json();
      setAllStateAndCity(jsonData);
    }
    const fetchCountries = async () => {
      setIsCountryLoading(true);
      const data = [{"country_name" : "India"}];
      setCountries(data.map((country) => ({ label: country.country_name, value: country.country_name })));
      setIsCountryLoading(false);
    };
    setStateAndCity();
    fetchCountries();
  }, []);

  useEffect(() => {
    const fetchStates = async () => {
      if (country) {
        setIsStateLoading(true);
        let allStates = Object.entries(allStateAndCity).map(([key]) => ({
          "label": key,
          "value": key
        }));
        setStates(allStates);
        setIsStateLoading(false);
      } else {
        setStates([]);
      }
    };
    if (allStateAndCity) {
      fetchStates();
    }

  }, [allStateAndCity, country]);

  const validateForm = () => {

    const isUserNameValid = address.trim() !== "";
    const isCountryValid = country === "India";
    const isCityValid = city.trim() !== "";
    const isStateValid = state.trim() !== "";
    const isPinCodeValid = pinCode.length === 6;

    setIsFormValid(isUserNameValid && isCountryValid && isCityValid && isStateValid && isPinCodeValid);
  };

  useEffect(() => {
    validateForm();
  }, [address, country, city, state, pinCode]);

  useEffect(() => {
    const fetchCities = async () => {
      if (state) {
        setIsCityLoading(true);
        let allCities = Object.entries(allStateAndCity[state]).map(([key,value]) => ({
          "label": value.city_name,
          "value": value.city_name
        }));
        setCities(allCities);
        setIsCityLoading(false);
      } else {
        setCities([]);
      }
    };
    if (allStateAndCity) {
      fetchCities();
    }  
  }, [allStateAndCity, state]);

  const handleCountryChange = (selectedOption) => {
    setCountry(selectedOption.value);
    setState("");
    if(states){
      setStates([...states]);
    } 
    setCity("");
    if(cities){
      setCities([...cities]);
    }
  };

  const handleStateChange = (selectedOption) => {
    setState(selectedOption.value);
    setCity("");
    if(cities){
      setCities([...cities]);
    }
  };

  const handleCityChange = (selectedOption) => {
    setCity(selectedOption.value);
  };

  const validatePinCode = (event) => {
    const value = event.target.value;
    if(value.length !== 6){
      setErrors({ ...errors, ["zipcode"]: 'Zip Code must be 6 characters' });
    }else{
      setErrors({ ...errors, ["zipcode"]: '' });
    }
  }

  const validateAddress = (value) => {
    if(value.trim().length <= 0){
      setErrors({ ...errors, ["address"]: 'Address must not be empty' });
    }else{
      setErrors({ ...errors, ["address"]: '' });
    }
  }

  const handlePinCodeKeyPress = (event) => {
    const keyCode = event.keyCode || event.which;
    const keyValue = String.fromCharCode(keyCode);
    if (/[^0-9]/.test(keyValue)) {
      event.preventDefault();
    }
  };

  const handlePinCodeChange = (event) => {
    const value = event.target.value;
    if (/^[0-9]*$/.test(value)) {
      setPinCode(value);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      address,
      city,
      state,
      country,
      pinCode,
      isEditMode,
      addressId
    });
  };

  const customStyles = {
    menu: (provided, state) => ({
      ...provided,
      maxHeight: "150px",
      overflow: "hidden",
    }),
    control: (provided, state) => ({
      ...provided,
      cursor: 'pointer'
    }),
    option: (provided, state) => ({
        ...provided,
        cursor: 'pointer'
      })
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2" htmlFor="address">
          Address
        </label>
        <input
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors && errors["address"] ? "border-red-500" : ""}`}
          id="address"
          type="text"
          placeholder="Enter your address"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          onBlur={(e) => validateAddress(e.target.value)}
        />
         {errors && errors["address"] && <div className="error text-red-500 text-xs">{errors["address"]}</div>}
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="country">
          Country
        </label>
        <Select options={countries}  isLoading={isCountryLoading}
      placeholder={isCountryLoading ? 'Loading Countries...' : 'Select an option'} onChange={handleCountryChange}  getOptionValue={(option) => option.label} value={countries.find((c) => c.value === country)} />
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="state">
          State
        </label>
        <Select options={states}  isLoading={isStateLoading}
      placeholder={isStateLoading ? 'Loading States...' : 'Select an option'} onChange={handleStateChange}  getOptionValue={(option) => option.label} styles={customStyles} value={state ? states.find((s) => s.value === state) : ""} />
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="city">
          City
        </label>
        <Select options={cities}  isLoading={isCityLoading}
      placeholder={isCityLoading ? 'Loading Cities...' : 'Select an option'} onChange={handleCityChange}  getOptionValue={(option) => option.label} styles={customStyles} value={city ? cities.find((c) => c.value === city) : ""} />
      </div>
      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2" htmlFor="pinCode">
          Zip Code
        </label>
        <input
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${errors && errors["pinCode"] ? "border-red-500" : ""}`}
          id="pinCode"
          type="text"
          placeholder="Enter your zip code"
          value={pinCode}
          maxLength={6}
          onChange={(e) => handlePinCodeChange(e)}
          onKeyPress={(e) => handlePinCodeKeyPress(e)}
          onBlur={(e) => validatePinCode(e)}
        />
         {errors && errors["zipcode"] && <div className="error text-red-500 text-xs">{errors["zipcode"]}</div>}
      </div>
      <button
        className="disabled:opacity-25 disabled:cursor-not-allowed bg-purple-600 hover:bg-purple-900 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit"
        disabled={!isFormValid || isSubmitButtonDisabled}
      >
        {isEditMode ? "Update" : "Save"}
      </button>
    </form>
  );
};

export default AddressForm;
