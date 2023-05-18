import React, { useEffect, useState } from "react";
import Select from "react-select";

const AddressForm = ({ onSave }) => {
  const [address, setAddress] = useState("");
  const [zipCode, setZipCode] = useState("");


  const [country, setCountry] = useState("");
  const [state, setState] = useState("");
  const [city, setCity] = useState("");

  // Fetch list of countries and states from API
  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  const[isCountryLoading, setIsCountryLoading] = useState(false);
  const[isStateLoading, setIsStateLoading] = useState(false);
  const[isCityLoading, setIsCityLoading] = useState(false);

  useEffect(() => {
    const fetchCountries = async () => {
      setIsCountryLoading(true);
      // const response = await fetch("https://www.universal-tutorial.com/api/countries",{
      //   method: 'GET',
      //   headers: {
      //     'Accept': 'application/json',
      //     'Authorization': 'Bearer ' + import.meta.env.VITE_COUNTRIES_UNIVERSAL_AUTH
      //   }
      // });
      // const data = await response.json();
      const data = [{"country_name" : "India"}];
      setCountries(data.map((country) => ({ label: country.country_name, value: country.country_name })));
      setIsCountryLoading(false);
    };
    fetchCountries();
  }, []);

  useEffect(() => {
    const fetchStates = async () => {
      if (country) {
        setIsStateLoading(true);
        // const response = await fetch(`https://www.universal-tutorial.com/api/states/${country}`,{
        //   method: 'GET',
        //   headers: {
        //     'Accept': 'application/json',
        //     'Authorization': 'Bearer ' + import.meta.env.VITE_COUNTRIES_UNIVERSAL_AUTH
        //   }
        // });
        // const data = await response.json();
        const data = [{"state_name" : "Tamil Nadu"}];
        setStates(data.map((state) => ({ label: state.state_name, value: state.state_name })));
        setIsStateLoading(false);
      } else {
        setStates([]);
      }
    };
    fetchStates();
  }, [country]);

  useEffect(() => {
    const fetchCities = async () => {
      if (state) {
        setIsCityLoading(true);
        // const response = await fetch(`https://www.universal-tutorial.com/api/cities/${state}`,{
        //   method: 'GET',
        //   headers: {
        //     'Accept': 'application/json',
        //     'Authorization': 'Bearer ' + import.meta.env.VITE_COUNTRIES_UNIVERSAL_AUTH
        //   }
        // });
        // const data = await response.json();
        const data = [{"city_name" : "Chennai"}];
        setCities(data.map((city) => ({ label: city.city_name, value: city.city_name })));
        setIsCityLoading(false);
      } else {
        setCities([]);
      }
    };
    fetchCities();
  }, [state]);

  const handleCountryChange = (selectedOption) => {
    setCountry(selectedOption.value);
    setState("");
  };

  const handleStateChange = (selectedOption) => {
    setState(selectedOption.value);
  };

  const handleCityChange = (selectedOption) => {
    setCity(selectedOption.value);
  };

  const validateZipCode = (zip) => {

  }

  const handleZipCodeKeyPress = (event) => {
    const keyCode = event.keyCode || event.which;
    const keyValue = String.fromCharCode(keyCode);
    if (/[^0-9]/.test(keyValue)) {
      event.preventDefault();
    }
  };

  const handleZipCodeChange = (event) => {
    const value = event.target.value;
    if (/^[0-9]*$/.test(value)) {
      setZipCode(value);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      address,
      city,
      state,
      country,
      zipCode
    });
  };

  const customStyles = {
    menu: (provided, state) => ({
      ...provided,
      maxHeight: "150px",
      overflow: "hidden",
    })
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2" htmlFor="address">
          Address
        </label>
        <input
          className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="address"
          type="text"
          placeholder="Enter your address"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
        />
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="country">
          Country
        </label>
        <Select options={countries}  isLoading={isCountryLoading}
      placeholder={isCountryLoading ? 'Loading Countries...' : 'Select an option'} onChange={handleCountryChange} value={countries.find((c) => c.value === country)} />
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="state">
          State
        </label>
        <Select options={states}  isLoading={isStateLoading}
      placeholder={isStateLoading ? 'Loading States...' : 'Select an option'} onChange={handleStateChange}styles={customStyles} value={states.find((s) => s.value === state)} />
      </div>
      <div className="mb-4">
      <label className="block text-gray-700 font-bold mb-2" htmlFor="city">
          City
        </label>
        <Select options={cities}  isLoading={isCityLoading}
      placeholder={isCityLoading ? 'Loading Cities...' : 'Select an option'} onChange={handleCityChange}styles={customStyles} value={cities.find((s) => s.value === city)} />
      </div>
      <div className="mb-4">
        <label className="block text-gray-700 font-bold mb-2" htmlFor="zipCode">
          Zip Code
        </label>
        <input
          className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="zipCode"
          type="text"
          placeholder="Enter your zip code"
          value={zipCode}
          onChange={(e) => handleZipCodeChange(e)}
          onKeyPress={(e) => handleZipCodeKeyPress(e)}
        />
      </div>
      <button
        className="bg-purple-600 hover:bg-purple-900 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit"
      >
        Save
      </button>
    </form>
  );
};

export default AddressForm;
