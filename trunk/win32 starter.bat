cd data
java -Xss2m -Xms512m -Xmx512m -Djava.library.path="native/win32" -cp lib/audiolib/vorbisspi1.0.3.jar;lib/audiolib/tritonus_share.jar;lib/audiolib/jorbis-0.0.15.jar;lib/audiolib/jogg-0.0.7.jar;lib/jogl.jar;lib/lwjgl.jar;lib/lwjgl_util.jar;. main.SuperMain


