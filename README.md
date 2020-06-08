# jgit-test
Setup:
```
#1
mkdir test-repo 
cd test-repo 

#2
git init

#3
echo "a" > 1.txt
echo "b" >> 1.txt
echo "c" >> 1.txt

#4
git add .
git commit -m 'init'
 
#5
echo "d" >> 1.txt
```
Compute diff:
```
> git diff HEAD
diff --git a/test.txt b/test.txt
index de98044..d68dd40 100644
--- a/test.txt
+++ b/test.txt
@@ -1,3 +1,4 @@
 a
 b
 c
+d
```

JGit output:
```
diff --git a/test.txt b/test.txt
index de98044..1a5f7ef 100644
--- a/test.txt
+++ b/test.txt
@@ -1,3 +1,4 @@
-a
-b
-c
+a
+b
+c
+d
```
