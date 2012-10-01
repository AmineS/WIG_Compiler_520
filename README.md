<<<<<<< HEAD
This file will contain our project description when it goes on Github
=======
Hi Folks this is the procedure to follow in order to get Git SVN working:

> You need the following packages installed:
	- git-svn > the real deal
	- gitk > GUI for Git
   Contact the CS help desk since you dont have permissions.

> Send me your public keys so you can have access to this private repo

> Create a folder, I called mine WIG_Compiler_520
 
> Open the terminal, navigate to the above folder and enter these commands:
	- git svn clone svn+ssh://cs520@svn.cs.mcgill.ca/2012/groups/group-h group-h

> Now navigate inside the folder group-h
	- git remote add origin https://github.com/AmineS/WIG_Compiler_520.git
	-git pull https://github.com/AmineS/WIG_Compiler_520

> At this point you should have a fully up to date version of the code.

> I created a new workspace, Its pointing at the folder created above

> I added a new java project where group-h exists but did not push the src or bin folder to Git.

> create a .gitignore file inside group-h and add to it two lines:
	.classpath
	.project

> for Git coloring put the following in your ~/.gitconfig

color]
        diff = auto
        status = auto
        branch = auto
[format]
        pretty = "Commit:  %C(yellow)%H%nAuthor:  %C(green)%aN <%aE>%nDate:    (%C(red)%ar%Creset) %ai%nSubject: %s%n%n%b"

> Ideally we want to have a master branch, 3 personal branches and one development branch. This is the reasoning behind this breakdown:
	- master branch is going to always have a clean version of the code that we can submit
	- development branch as the name implies is used throughout the development phase and might be broken from time to time
	- personal branches let you wander around and try new things without affecting or being affected by others

> Here are the links I used for Git-SVN

http://andy.delcambre.com/2008/03/04/git-svn-workflow.html

http://caiustheory.com/adding-a-remote-to-existing-git-repo

http://thomashunter.name/blog/git-colored-output-shortcut-commands-autocompletion-and-bash-prompt/

https://help.github.com/articles/ignoring-files

http://thomashunter.name/blog/git-colored-output-shortcut-commands-autocompletion-and-bash-prompt/

http://maymay.net/blog/2009/02/24/how-to-use-git-svn-as-the-only-subversion-client-youll-need/

> Also read up on Git: Basics and Braching...


>>>>>>> Added to README the steps of using Git SVN
