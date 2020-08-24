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
Visualization and evaluation: We are thinking that these should not be part of the app but rather our finidings and measurements that we did in our app. These should be included in the final report.

# 3 Concept
Design the main layout
 	
Read Photo Information from 	Passports using NFC. --> Take photo Button becomes active
 	
Take Photo with Camera ( Fixed 	Ration with Guidelines becomes active)
 	
Matching Button becomes active
 	
Matching
 	
If no matching raise an alert 	message
 	
If matching, then perform 	De-Morphing



# 4 Implementation
Same as concept but very detailed


# 5 Evaluation
Evaluation of what was implemented in the previous step.
Security Risks involved with the usage of the app.
Graphs and tables with our results go here.


# Some Resources
- The_magic_passport   -  https://ieeexplore.ieee.org/document/6996240

- Face_demorphing  - https://ieeexplore.ieee.org/document/8119561

- Face demorphing in the presence of facial appearance variations   -  https://ieeexplore.ieee.org/abstract/document/8553430

- FD-GAN - https://ieeexplore.ieee.org/abstract/document/8730323

- Detection of Face Morphing Attacks by Deep Learning - https://link.springer.com/chapter/10.1007/978-3-319-64185-0_9

- Generative Adversarial Networks - https://ieeexplore.ieee.org/abstract/document/8846232

- PRNU Analysis - https://ieeexplore.ieee.org/abstract/document/8846232

- A Survey - https://ieeexplore.ieee.org/abstract/document/8642312

- OVGU guys - https://dl.acm.org/doi/abs/10.1145/3335203.3335721
