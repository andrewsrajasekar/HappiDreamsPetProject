import React, { useEffect, useState } from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';

const MainPageCarousel = ({isPopUp, prePopulateImages}) => {

    const [carouselWidth, setCarouselWidth] = useState('100%');
    const [carouselHeight, setCarouselHeight] = useState('100%');

    const backendImages = [
      {
        id: 1,
        url: "https://dummyimage.com/1203x503"
      },
      {
        id: 2,
        url: "https://dummyimage.com/1203x503"
      },
      {
        id: 3,
        url: "https://dummyimage.com/1203x503"
      }
    ]

    const [images, setImages] = useState(prePopulateImages !== undefined ? prePopulateImages : backendImages);

    const renderedImages = images.map((image) => {
      return(
        <div key={image.id}>
            <img src={image.url} alt="Hot Topic 1" className="max-h-[600px]"  />
          </div>
      )
    })

    useEffect(() => {
      if(!isPopUp){
        const handleResize = () => {
          setCarouselWidth(`${getWidth()}px`);
        };
  
        const getWidth = () => {
          return document.getElementById("topProducts").offsetWidth;
        }
  
    
        // Set initial width
        handleResize();
    
        // Update width on window resize
        window.addEventListener('resize', handleResize);
    
        // Clean up event listener on component unmount
        return () => {
          window.removeEventListener('resize', handleResize);
        };
      }
    }, []);

  return (
    <div className="flex justify-center">
      <div className="w-full carousel-container m-5 mb-10" style={{ width: carouselWidth, height: carouselHeight }}>
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
          {renderedImages}
        </Carousel>
      </div>
    </div>
  );
};

export default MainPageCarousel;
