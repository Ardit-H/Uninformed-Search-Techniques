# Ekzekuton te tri programet Java (Java 21 i kompilon vetë). Perdorimi: .\run_all.ps1
$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

Write-Host "`n##### Latin Square (n = 5, IDDFS i plote) #####"
java "$root\latin_square\LatinSquare.java" 5
Write-Host "`n##### Sudoku (Easy, Medium, Hard) #####"
java "$root\sudoku\Sudoku.java"
Write-Host "`n##### Social Golfers (9 jave) #####"
java "$root\golfers\Golfers.java" 9 30