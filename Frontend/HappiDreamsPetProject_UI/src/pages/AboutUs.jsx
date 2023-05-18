import React from "react";

const AboutUs = () => {
    const companies = [
        {
            "id": 1,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo.jpg?v=1668421991",
            "name" : "Fiedele"
        },
        {
            "id": 2,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo1.jpg?v=1668422020",
            "name" : "Applaws"
        },
        {
            "id": 3,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo2.jpg?v=1668422062",
            "name" : "Arden Grange"
        },
        {
            "id": 4,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo3.jpg?v=1668422091",
            "name" : "Fish4Dogs"
        },
        {
            "id": 5,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo4.jpg?v=1668422195",
            "name" : "Kit Cat"
        },
        {
            "id": 6,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo5.jpg?v=1668422240",
            "name" : "Lara"
        },
        {
            "id": 7,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo6.jpg?v=1668422266",
            "name" : "Mera"
        },
        {
            "id": 8,
            "logo": "https://cdn.shopify.com/s/files/1/0080/7121/7204/files/logo7.jpg?v=1668422292",
            "name" : "mybeau"
        }
    ]
  return (
    <div className="py-20">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          <h2 className="text-base text-purple-600 font-semibold tracking-wide uppercase">
            About Us
          </h2>
          <p className="mt-2 text-3xl leading-8 font-extrabold tracking-tight text-gray-900 sm:text-4xl">
            Our Mission and Values
          </p>
          <p className="mt-4 max-w-2xl text-xl text-gray-500 lg:mx-auto">
            We are a company that is dedicated to providing top-quality services
            to our clients. Our mission is to deliver exceptional results that
            exceed our clients’ expectations.
          </p>
        </div>
        <div className="mt-20">
          <h2 className="text-base text-purple-600 font-semibold tracking-wide uppercase">
            Brands We Deal
          </h2>
          <div className="mt-6 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
            {companies.map((company, index) => (
              <div key={company.id} className={`col-span-1 flex justify-center py-8 border-solid border-black ${index % 4 === 0 ? (index === 0 ? 'border-2' : 'border-l-2 border-r-2 border-b-2') : (Math.floor(index / 4) > 0 ? 'border-r-2 border-b-2' : 'border-t-2 border-r-2 border-b-2' )}`}>
                <img
                  className="max-h-12"
                  src={company.logo}
                  alt={company.name}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AboutUs;
