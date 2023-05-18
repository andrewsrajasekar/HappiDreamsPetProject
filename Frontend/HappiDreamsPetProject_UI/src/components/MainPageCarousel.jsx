import React, { useEffect, useState } from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';

const MainPageCarousel = () => {

    const [carouselWidth, setCarouselWidth] = useState('100%');

    useEffect(() => {
      const handleResize = () => {
        setCarouselWidth(`${getWidth()}px`);
      };

      const getWidth = () => {
        return document.getElementById("topProducts").offsetWidth;
      }
  
      // Set initial width
      setCarouselWidth(`${getWidth()}px`);
  
      // Update width on window resize
      window.addEventListener('resize', handleResize);
  
      // Clean up event listener on component unmount
      return () => {
        window.removeEventListener('resize', handleResize);
      };
    }, []);

  return (
    <div className="flex justify-center">
      <div className="w-full carousel-container m-5 mb-10" style={{ width: carouselWidth }}>
        <Carousel
          showArrows={true}
          showStatus={false}
          showIndicators={false}
          autoPlay={true}
          infiniteLoop={true}
          interval={3000}
          transitionTime={500}
          emulateTouch={true}
        >
          <div>
            <img src="https://dummyimage.com/1203x503" alt="Hot Topic 1" />
          </div>
          <div>
            <img src="https://dummyimage.com/1203x503" alt="Hot Topic 2" />
          </div>
          <div>
            <img src="https://dummyimage.com/1203x503" alt="Hot Topic 3" />
          </div>
          {/* Add more image slides as needed */}
        </Carousel>
      </div>
    </div>
  );
};

export default MainPageCarousel;
