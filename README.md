# Demorpher
Summer Semester 2020 Project




# 1 Motivation and Problem Statement

The  application  follows  a  generic  workflow  as  describedbelow.For  developing  the  Android  application  to  detect  potentiallymorphed  passport  images,  we  started  by  portraying  a  layoutfor the application followed by implementing the features one by one. 

The steps are as follows:
- Read the travel document data (image in our case) usingNFC
- Capture a live image with device camera
- Perform a face match
- If necessary perform De-morphing process
- Visualization and Evaluation of Results

# 2 Tool set
For Reading face images from a document using NFC we are going to use jmrtd (https://jmrtd.org/)
For taking a live-image with the camera we used CameraX module using a fixed ratio with guidelines to make sure every face is in the same position
For matching we are using MobileFaceNet architecture with help of tensorflow lite and MTCNN for detection and place landmark on the face.
Please refer to the [report file](Report/Team_Project_SS2020.pdf) for detailed information.

# 3 Process

- First Scan the image from ePassport by clicking "NFC SCAN" Button. Enter required details and click on "SAVE IMAGE" button on right top corner.
- Then Live Camera Image can be taken by "CAPTURE" button.
- After saving both images, "Match" will be enabled and similarity between two faces can be measured after detecting the face ("DETECT") from images.
- Upon successful matching and similarity score more than predefined threshold, DEMORPH oprtion will be enabled.
- By pressing "DEMORPH" it will send the data to server and result with de-morphed image will be apear.
Note :For successful demorph process, the smartphone needs to be connected through university network.




# Resources
- [The_magic_passport](https://ieeexplore.ieee.org/document/6996240)

- [Face_demorphing](https://ieeexplore.ieee.org/document/8119561)

- [Face demorphing in the presence of facial appearance variations](https://ieeexplore.ieee.org/abstract/document/8553430)

- [FD-GAN](https://ieeexplore.ieee.org/abstract/document/8730323)

- [Detection of Face Morphing Attacks by Deep Learning](https://link.springer.com/chapter/10.1007/978-3-319-64185-0_9)

- [Generative Adversarial Networks](https://ieeexplore.ieee.org/abstract/document/8846232)

- [A Survey on demorphing processes](https://ieeexplore.ieee.org/abstract/document/8642312)

- [MTCNN for Android](https://github.com/vcvycy/MTCNN4Android)

- [MObileFaceNet](https://github.com/sirius-ai/MobileFaceNet_TF)

- [NFC Reading](https://github.com/AppliedRecognition/Passport-Reader-Android-Sample)
