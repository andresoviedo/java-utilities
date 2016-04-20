#!/bin/bash
android_IMG=android-x86-4.4-r3
android_ISO=$android_IMG.iso
new_android_ID=`date +%s`
new_android_VM=$android_IMG.$new_android_ID
new_android_VDI=$android_IMG.$new_android_ID.vdi
adb_port=5555
webdriver_port=5510

VBoxManage createhd --filename $new_android_VDI --size 2048 --variant Fixed

VBoxManage createvm --name $new_android_VM --ostype Linux26 --register --basefolder `pwd`
VBoxManage modifyvm $new_android_VM --memory 512

VBoxManage storagectl $new_android_VM --name IDE --add ide --controller PIIX4
VBoxManage storageattach $new_android_VM --storagectl IDE --port 0 --device 0 --type dvddrive --medium $android_ISO
VBoxManage storagectl $new_android_VM --name SATA --add sata --controller IntelAhci --bootable on
VBoxManage storageattach $new_android_VM --storagectl SATA --port 0 --type hdd --medium $new_android_VDI

VBoxManage modifyvm $new_android_VM --natpf1 "shell-adb,tcp,,$adb_port,,5555"
VBoxManage modifyvm $new_android_VM --natpf1 "webdriver,tcp,,$webdriver_port,,5010"

VBoxManage startvm $new_android_VM

