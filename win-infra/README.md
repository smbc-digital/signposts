Windows Infrastructure Automation
---------------------------------

Developers on Macs prereqs:
- Install Vagrant https://www.vagrantup.com/
- Install Virtualbox https://www.virtualbox.org/wiki/Downloads

Get going:
- `make windows` in the root dir to get going

Package on Windows
------------------

Several dependencies are used to build installers for Windows.

The service wrapper used is WinSW.
- http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/1.16/

The files are packaged using NSIS.
- http://nsis.sourceforge.net/Main_Page

The package manager used to setup the Windows build machine is:
- https://chocolatey.org/

