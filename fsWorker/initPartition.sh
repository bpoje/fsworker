dd if=/dev/zero of=./partition bs=1024 count=10240
mkfs.vfat -F 16 ./partition
