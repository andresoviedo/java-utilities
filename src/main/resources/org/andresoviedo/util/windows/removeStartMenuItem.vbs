On Error Resume Next

Set objFS = CreateObject("Scripting.FileSystemObject")
Set objShell = WScript.CreateObject("WScript.Shell")

path = WScript.Arguments(0)
specialfolder = WScript.Arguments(1)

WScript.StdOut.WriteLine("path: <" & specialfolder & ">" & path)

Dim strStartMenu
strStartMenu = objShell.SpecialFolders("AllUsersPrograms" )
If specialfolder = "Programs" Then
	strStartMenu = objFS.GetParentFolderName(objShell.SpecialFolders("Startup"))
End If
strStartMenu = objFS.BuildPath(strStartMenu,path)

WScript.StdOut.WriteLine("removing: " & strStartMenu & "...")
If objFS.FolderExists(strStartMenu) Then
	objFS.DeleteFolder(strStartMenu)
	WScript.StdOut.WriteLine("removed: " & strStartMenu)
End If
