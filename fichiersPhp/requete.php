<?php
require("auth.php");
require("fonction.php");

//USER-PASSWORD----------------------------------------------------------------------------------------------------

//demande la liste des users

if(isset($_POST['liste_user'])){
    echo json_encode(list_user());
}

//vérifie si l'inscription est possible et enregistre le nouveau user sinon error

else if(isset($_POST['inscrit']) && isset($_POST['mdp'])){
    $inscrit = $_POST['inscrit'];
    $mdp = $_POST['mdp'];
    if(!is_exist($inscrit)){
	    user_inscription($inscrit,$mdp);
        $json['success'] = 'Welcome '.$inscrit;
    }else{ 
        $json['error'] = 'Error '.$inscrit;
    }
    echo json_encode($json);
}

//demande si username et password se trouve ou non dans la BD

else if(isset($_POST['username']) && isset($_POST['password'])){
    $username = $_POST['username'];
    $password = $_POST['password'];
    if(is_user_exist($username, $password)){
        $json['success'] = 'Welcome '.$username;
    }else{ 
        $json['error'] = 'Error '.$username;
    }
    echo json_encode($json);
}
//Category

else if(isset($_POST['category_list'])){
    echo json_encode(category_list());
}

//LIST------------------------------------------------------------------------------------------------------

//demande la liste des listes

else if(isset($_POST['liste_liste']) && isset($_POST['category']) && isset($_POST['option']) && isset($_POST['value'])){
    $category = $_POST['category'];
    $option = $_POST['option'];
    $value = $_POST['value'];
    if($category == 'all'){
        echo json_encode(list_list2($option,$value));
    }else echo json_encode(list_list3($category, $option, $value));
}

else if(isset($_POST['liste_liste']) && isset($_POST['option']) && isset($_POST['value'])){
    $option = $_POST['option'];
    $value = $_POST['value'];
    echo json_encode(list_list2($option,$value));
}

else if(isset($_POST['liste_liste'])){
    echo json_encode(list_list());
}

//ajouter une liste dans liste des listes

else if(isset($_POST['ajoutlist']) && isset($_POST['username']) && isset($_POST['icon']) && isset($_POST['category']) && isset($_POST['visibility']) && isset($_POST['listcolor']) && isset($_POST['creation']) && isset($_POST['modified']) && isset($_POST['reminder']) && isset($_POST['reminderTime'])){
    $newlist = $_POST['ajoutlist'];
    $username = $_POST['username'];
    $icon = $_POST['icon'];
    $category = $_POST['category'];
    $visibility = $_POST['visibility'];
    $listcolor = $_POST['listcolor'];
    $creation = $_POST['creation'];
    $modified = $_POST['modified'];
    $reminder = $_POST['reminder'];
    $reminderTime = $_POST['reminderTime'];
    if(!is_in_list($newlist)){
	    add_list_list($newlist, $username, $icon, $category, $visibility,$listcolor,$creation, $modified, $reminder, $reminderTime);
        $json['success'] = 'Success '.$newlist;
    }else{ 
        $json['error'] = 'Error '.$newlist;
    }
    echo json_encode($json);
}

//Check list
else if (isset($_POST['checkList']) && isset($_POST['checked'])){
    $list = $_POST['checkList'];
    $checked = $_POST['checked'];
    if(is_in_list($list)){
    check_list($list, $checked);
        $json['success'] = 'Success '.$list;
    }else{
        $json['error'] = 'Error '.$list;
    }
    echo json_encode($json);
}

//Check element
else if (isset($_POST['checkElement']) && isset($_POST['checked']) && isset($_POST['listName'])){
    $element = $_POST['checkElement'];
    $checked = $_POST['checked'];
    $listName = $_POST['listName'];
    if(is_in_element($element) && is_in_list($listName)){
    check_element($element, $listName, $checked);
        $json['success'] = 'Success '.$element;
    }else{
        $json['error'] = 'Error '.$element;
    }
    echo json_encode($json);
}

//supprimer la liste l et les éléments qui s'y trouve

else if (isset($_POST['listToSupp'])){
    $li = $_POST['listToSupp'];
    if(is_in_list($li)){
	supp_list($li);
        $json['success'] = 'Success '.$li;
    }else{
        $json['error'] = 'Error '.$li;
    }
    echo json_encode($json);
}


//modifier la liste l

else if(isset($_POST['modList']) && isset($_POST['lastList']) && isset($_POST['icon']) && isset($_POST['category']) && isset($_POST['visibility']) && isset($_POST['listcolor']) && isset($_POST['modified']) && isset($_POST['reminder']) && isset($_POST['reminderTime'])){
    $m = $_POST['modList'];
    $lm = $_POST['lastList'];
    $icon = $_POST['icon'];
    $category = $_POST['category'];
    $visibility = $_POST['visibility'];
    $listcolor = $_POST['listcolor'];
    $modified = $_POST['modified'];
    $reminder = $_POST['reminder'];
    $reminderTime = $_POST['reminderTime'];
    if(is_in_list($lm)){
	mod_list($m, $lm, $icon, $category, $visibility, $listcolor, $modified, $reminder, $reminderTime);
        $json['success'] = 'Success '.$m;
    }else{ 
        $json['error'] = 'Error '.$m;
    }
    echo json_encode($json);
}

else if(isset($_POST['favList']) && isset($_POST['heart'])){
    $fav = $_POST['favList'];
    $heart = $_POST['heart'];
    if(is_in_list($fav)){
    fav_list($fav, $heart);
        $json['success'] = 'Success '.$fav;
    }else{ 
        $json['error'] = 'Error '.$fav;
    }
    echo json_encode($json);
}

else if(isset($_POST['pinList']) && isset($_POST['pin'])){
    $pinList = $_POST['pinList'];
    $pin = $_POST['pin'];
    if(is_in_list($pinList)){
    pin_list($pinList, $pin);
        $json['success'] = 'Success '.$pinList;
    }else{ 
        $json['error'] = 'Error '.$pinList;
    }
    echo json_encode($json);
}



//ELEMENT-------------------------------------------------------------------------------------------------

//demande la liste des élements dans une liste

else if(isset($_POST['liste'])){
    $liste = $_POST['liste'];
    echo json_encode(list_element($liste));
}

//ajouter une element dans element_list (element de la liste l)

else if(isset($_POST['ajoutelement']) && isset($_POST['l']) && isset($_POST['username']) && isset($_POST['description']) && isset($_POST['ecolor']) && isset($_POST['priority']) && isset($_POST['creation'])){
    $newelement = $_POST['ajoutelement'];
    $l = $_POST['l'];
    $username = $_POST['username'];
    $description = $_POST['description'];
    $ecolor = $_POST['ecolor'];
    $priority = $_POST['priority'];
    $creation = $_POST['creation'];
    if(!is_in_elementlist($newelement, $l)){
	add_element_list($newelement, $l, $username, $description, $ecolor, $priority, $creation);
        $json['success'] = 'Success '.$newelement;
    }else{ 
        $json['error'] = 'Error '.$newelement;
    }
    echo json_encode($json);
}

//supprimer une element dans element_list (element de la liste l)

else if (isset($_POST['s']) && isset($_POST['li'])){
    $s = $_POST['s'];
    $li = $_POST['li'];
    if(is_in_elementlist($s, $li)){
	    supp_element_list($s, $li);
        $json['success'] = 'Success '.$s;
    }else{
        $json['error'] = 'Error '.$s;
    }
    echo json_encode($json);
}


//modifier une element dans element_list (element de la liste l)

else if(isset($_POST['newElement']) && isset($_POST['list']) && isset($_POST['lastElement']) && isset($_POST['description']) && isset($_POST['ecolor']) && isset($_POST['priority']) && isset($_POST['modified'])){
    $m = $_POST['newElement'];
    $lm = $_POST['list'];
    $e = $_POST['lastElement'];
    $description = $_POST['description'];
    $ecolor = $_POST['ecolor'];
    $priority = $_POST['priority'];
    $modified = $_POST['modified'];
    if(is_in_elementlist($e, $lm)){
	    mod_element_list($m, $lm, $e, $description, $ecolor, $priority, $modified);
        $json['success'] = 'Success '.$m;
    }else{ 
        $json['error'] = 'Error '.$m;
    }
    echo json_encode($json);
}


//HISTORIQUE---------------------------------------------------------------------------------------------

//Historique

else if(isset($_POST['hisL'])&&isset($_POST['hisE']) && isset($_POST['hisU']) && isset($_POST['hisA']) && isset($_POST['hisD'])){
    $hisL = $_POST['hisL'];
    $hisE = $_POST['hisE'];
    $hisU = $_POST['hisU'];
    $hisA = $_POST['hisA'];
    $hisD = $_POST['hisD'];
    $json[addHisto($hisL,$hisE,$hisU,$hisA,$hisD)] = 'Welcome';
    echo json_encode($json);
}

else if(isset($_POST['histoES']) && isset($_POST['value'])){
    echo json_encode(showhistoES($_POST['value']));
}

else if(isset($_POST['histoEO']) && isset($_POST['value'])){
    echo json_encode(showhistoEO($_POST['value']));
}

else if(isset($_POST['histo'])){
    echo json_encode(showhisto());
}

else if(isset($_POST['h'])&&isset($_POST['lh'])&&isset($_POST['eh'])){
    $h = $_POST['h'];
    $lh = $_POST['lh'];
    $eh = $_POST['eh'];
    $json[historique($h, $lh, $eh)] = 'Welcome';
    echo json_encode($json);
}

else if(isset($_POST['historique'])){
	/*
	$var = showhistorique();	
	$indexedOnly = $var;
	
	foreach ($associative as $row) {
    		$indexedOnly[] = array_values($row);
	}*/
	//var_dump(showhistorique());
    echo json_encode(showhistorique());
}

?>