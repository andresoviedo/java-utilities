Set objFS = CreateObject("Scripting.FileSystemObject")
Set objShell = WScript.CreateObject("WScript.Shell")

Dim path
Dim lnk
Dim args
Dim desc

path = WScript.Arguments(0)
lnk = WScript.Arguments(1)
args = WScript.Arguments(2)
desc = WScript.Arguments(3)
specialfolder = WScript.Arguments(4)

WScript.StdOut.WriteLine("path: " & specialfolder & "-->" & path)
WScript.StdOut.WriteLine("lnk: " & lnk)
WScript.StdOut.WriteLine("args: " & args)
WScript.StdOut.WriteLine("desc: " & desc)

Dim strStartMenu
strStartMenu = objShell.SpecialFolders("AllUsersPrograms" )
If specialfolder = "Programs" Then
	strStartMenu = objFS.GetParentFolderName(objShell.SpecialFolders("Startup"))
End If
strStartMenu = objFS.BuildPath(strStartMenu,path)

If Not objFS.FolderExists(strStartMenu) Then
	objFS.CreateFolder(strStartMenu)
ElseIf objFS.FileExists(strStartMenu) Then
	objFS.DeleteFile(strStartMenu)
End If

InstallStartMenuLink path,lnk,args,desc

Function InstallStartMenuLink(path,lnk,args,desc)	
	newLnk = objFS.BuildPath(strStartMenu,desc+".lnk")
	set oShellLink = objShell.CreateShortcut(newLnk)
	oShellLink.TargetPath = lnk
	oShellLink.Arguments = args
	oShellLink.WindowStyle = 1
	oShellLink.IconLocation = lnk
	oShellLink.Description = desc
	oShellLink.WorkingDirectory = objFS.GetParentFolderName(lnk)
	oShellLink.Save
End Function
