CopyRecursiveFolder ".\src", ".\dst"

Function CopyRecursiveFolder(rootFolderSpec,targetFolderSpec)
	Set objFS = CreateObject("Scripting.FileSystemObject")
	If Not objFS.FolderExists(targetFolderSpec) Then
		objFS.CreateFolder(targetFolderSpec)
	End if
	Set rootFolder = objFS.GetFolder(rootFolderSpec)
	Set folderFiles = rootFolder.Files
	For Each folderFile in folderFiles
		objFS.CopyFile objFS.BuildPath(rootFolderSpec,folderFile.name), _
			objFS.BuildPath(targetFolderSpec,folderFile.name)
	Next
	Set subfolders = rootFolder.SubFolders
	For Each subfolder in subfolders
		CopyRecursiveFolder objFS.BuildPath(rootFolderSpec,subfolder.name), _
			objFS.BuildPath(targetFolderSpec,subfolder.name)
	Next
End Function