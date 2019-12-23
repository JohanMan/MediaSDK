# 设置版本号
cmake_minimum_required(VERSION 3.4.1)

# 路径参数
set(CPP_PATH  ${CMAKE_SOURCE_DIR}/src/main/cpp)
set(SO_PATH  ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})

# 包含include文件
include_directories(${CPP_PATH}/include)

# 配置OpenMP
find_package(OpenMP REQUIRED)
if(OPENMP_FOUND)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${OpenMP_EXE_LINKER_FLAGS}")
endif()

# 添加 OpenCV SO
add_library( libopencv_java3 SHARED IMPORTED )
set_target_properties( libopencv_java3
                       PROPERTIES IMPORTED_LOCATION
                       ${SO_PATH}/libopencv_java3.so )

# 添加 NCNN A
add_library( libncnn STATIC IMPORTED )
set_target_properties( libncnn
                       PROPERTIES IMPORTED_LOCATION
                       ${SO_PATH}/libncnn.a )

# 日志库
find_library( log-lib
              log )

# dlib
include(${CPP_PATH}/dlib/cmake)

# 自定义库
add_library( android-opencv
             SHARED
             ${CPP_PATH}/android_opencv.cpp
             ${CPP_PATH}/face_detector.cpp
             ${CPP_PATH}/opencv_util.cpp
             ${CPP_PATH}/android_util.cpp
             ${CPP_PATH}/c_util.cpp
             ${CPP_PATH}/mtcnn.cpp )

# 链接
target_link_libraries( android-opencv
                       libopencv_java3
                       dlib
                       libncnn
                       android
                       jnigraphics
                       ${log-lib} )