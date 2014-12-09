# FAT16 Steganography

Eclipse java project:
* GUI for hiding data in FAT16
* VFAT support (LFN - long filenames)

Data can be hidden in:
* file slack
* fake bad clusters

## About FAT

In order for FAT to manage files with satisfactory efficiency, it groups sectors into larger blocks referred to as clusters. A cluster is the smallest unit of disk space that can be allocated to a file, which is why clusters are often called allocation units. Clusters are groups of consecutive sectors (usually 512 B). File slack is data that starts from the end of the file written and continues to the end of the sectors designated to the file.

(http://www.sleuthkit.org/informer/sleuthkit-informer-18.html)
(http://www.forensicswiki.org/wiki/FAT)

## Output log example
Program generates log that allows user to later reconstruct the hidden files:
```
Input file:test1/163840/5b28cb5d0651fe6999a810c47100e580
Hidden in:
c/65536/65536/072c396e6b9dc02c1039fd9b2f29baf1/2
f/45056/45056/ffacdaa662af0578acac3e1d8f3ff0e4/Archive/Parrot.7z.001
f/45056/45056/ef3661b95935982f84516358718784cd/Archive/Parrot.7z.002
c/8192/65536/1483613f9929cbad55e19f8bbbfe8048/754
```

## Screenshot
![Alt text](/screenshot/screenshot.jpg "Screenshot")
