#rekurzivno listanje

rekurzija () {

	for FILE in `ls $1`
	do

	if [ -f $FILE ]
	then
	echo "Datoteka: $FILE"
	
	elif [ -d $FILE ]	
	then
	echo "Direktorium: $FILE"
	cd $FILE
	rekurzija .
	cd ..

	fi	
	done

}

rekurzija .

#izlistaj go rekurzivno tekoviot direktorium
