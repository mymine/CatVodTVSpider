rm -f custom_spider.jar
rm -rf Smali_classes

java -jar 3rd/baksmali-2.5.2.jar d ../app/build/intermediates/dex/release/minifyReleaseWithR8/classes.dex -o Smali_classes

rm -rf spider.jar/smali/com/github/catvod/spider
rm -rf spider.jar/smali/com/github/catvod/parser

[ -f spider.jar/smali/com/github/catvod ] || mkdir -p spider.jar/smali/com/github/catvod

if [ "$1" == "ec" ]; then
    java -Dfile.encoding=utf-8 -jar 3rd/oss.jar Smali_classes
fi

mv Smali_classes/com/github/catvod/spider spider.jar/smali/com/github/catvod
mv Smali_classes/com/github/catvod/parser spider.jar/smali/com/github/catvod

rm -rf Smali_classes

java -jar 3rd/apktool_2.4.1.jar b spider.jar -c

mv spider.jar/dist/dex.jar custom_spider.jar

md5sum custom_spider.jar | cut -d ' ' -f 1 > custom_spider.jar.md5

rm -rf spider.jar/smali/com/github/catvod/spider
rm -rf spider.jar/smali/com/github/catvod/parser

rm -rf spider.jar/build
rm -rf spider.jar/dist
