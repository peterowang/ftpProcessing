日期参数不指定则默认处理咋天的csv文件，如果指定为all，则处理所有的日期.

java -jar {name.jar} 无参数时，下载指定的ftp文件到本地,处理昨天上传的文件...
java -jar {name.jar} local 参数为local时，处理本地目录下指定文件中,昨天上传的文件 
java -jar {name.jar} allfile 参数为allfile时，获取ftp指定路径下所有昨天的csv,进行处理
java -jar {name.jar} local allfile 参数为两个，分别是local  allfile时,获取本地指定路径下所有咋天的csv,进行处理
java -jar {name.jar} local 20170505 从本地读取指定文件中指定日期的csv进行处理
java -jar {name.jar} allfile 20170505 从ftp指定路径下获取所有指定日期的文件并进行处理
java -jar {name.jar} local allfile 20170505 从本地指定路径下所有指定日期的csv并进行处理
java -jar {name.jar} 20170505 从ftp下获取指定文件并处理指定日期的文件
