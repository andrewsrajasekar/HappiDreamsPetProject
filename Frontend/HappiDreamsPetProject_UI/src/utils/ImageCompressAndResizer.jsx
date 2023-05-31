import FileResizer from 'react-image-file-resizer';
function compressAndResizeImage(file, maxWidth, maxHeight, outputFormat){
  return new Promise((resolve, reject) => {
    FileResizer.imageFileResizer(
      file, // Input file
      maxWidth, // Max width
      maxHeight, // Max height
      outputFormat, // Output format
      90, // Quality
      0, // Rotation
      (uri) => {
        resolve(uri);
      },
      'base64', // Output type
      maxWidth,
      maxHeight
    );
  });

//   return new Promise(resolve => {
//     Resizer.imageFileResizer(file, maxWidth, maxHeight, outputFormat, 100, 0,
//     uri => {
//       resolve(uri);
//     }, 'base64' );
// });
};

export default compressAndResizeImage;
