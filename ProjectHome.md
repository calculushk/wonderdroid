# What is this? ワンダードロイドってなに？ #

An emulator of the Bandai wonderswan/wonderswan color handheld games consoles for Android.

バンダイのワンダースワンというハードのエミュです。

# How to use it #

  1. Open wonderdroid at least once so that it can create the directories for roms etc
  1. Copy your game roms in either zip archives or plain .wsc or .ws files onto your sd card **1** in the wonderdroid directory that has been created there. Zips with multiple files should work but it's not been tested.
  1. Open wonderdroid again, it should now have a gallery of the roms you put into it's directoy. Wonderdroid uses the metadata in the rom to select a screenshot.. if a game has a question mark it will probably still work. It just hasn't been screenshot'ed yet

**1** - Some phones like the Samsung Galaxy S2/S3 put a block of internal storage where the sd card normally is. If you have such a phone you need to copy roms to the directory created in the internal storage instead. The application asks android for the location of the "external storage" which is usually the sd card but in these phones it's "internal external storage".

# 使い方 #

  1. インストールしたあと、一回ワンダードロイドを開けてください。そうすれば自動にsdカードに必要なフォルダが作られます。
  1. sdカードにあるwonderdroidというフォルダにロムファイルをコピーしてください。.zipと.wscと.wsが使えます。Galaxy S2・Galaxy S3などを持ってる方は、sdカードというのが二つあります。内部sdカードと外部sdカードがあります。今のバージョンは内部のしか使えません。
  1. ワンダードロイドのフォルダにロムファイルをコピーしたあと、またワンダードロイドを開けたら、フォルダに入ってるゲームがメニューに出ます。好きなゲームを選んでクリックすれば遊べます。

# Silly things people email me about #

  * Wonderdroid cannot play GBA, DS, Playstation 3 etc games. I get at least one email about this a week. Sometimes I get people that want me to find emulators for those systems for them.. the market has a built in search feature.. use it.

  * Don't email me begging for roms. The games aren't mine to send you and if you can't use google yourself that's your own problem.

  * Wonderdroid does have some bugs/issues that I'm aware of. I sometimes get nice e-mail about them which I don't mind. Sometimes I get lists of demands saying that I should do such and such right now or else.. If you have an issue that makes wonderdroid totally unusable (like the crash on Android 4.2 that was fixed recently) then please email me about it or use the issue tracker here and I will probably fix it. I don't have lots of time to implement lots of features or fix stuff like sound stuttering on slow phones. Maybe sometime in the future.

# Development stuff #


**I am currently looking for people to help with this, I don't have the time to do much work on it. I plan to port it to SDL1.3.. if anyone is interested in helping out drop me a line**

Current Market Release 1.7e

Road Map for 1.7f:
  * Support for Zipped ROMs (Done)
