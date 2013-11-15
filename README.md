Discount Finder
==============
Some hints on git
For now we have two branches master is master and devel is for development!
we do our thing! in devel branch and then when we satisfied and reached a reasonable point we merge back to master.
One could also create a personal branch and merge it with devel or master every often, For doing so:
$git branch -b [name of branch]

First time for cloning the repo:
```$git clone https://github.com/sasan-j/discountFinder.git```
Or if you have your ssh key setup:
```$git clone git@github.com:sasan-j/discountFinder.git```

Changing working branch:
```$git checkout [name of branch]   # in our case currently master and devel```

For receiving all changes from repository(on github)
```$git fetch```

Please note that when you copy some new files into repo directory or create new files you have to add them so git can keep track of them. 
```$git add [your file or directory- will be added recursively]```
For example for adding a folder called images
```$git add images```
For adding everything
```$git add .```

After adding files one have commit the changes to make them consistent
```$git commit -m “Some description about what you have done”```

Even if you added files before, when you do some modification you to add them again, this is called adding them stage, when you stage one file it will be included in your next commit. but if you are lazy you can do:
```$git commit -a -m “bla bla bal”```

