<?php

//USER-PASSWORD----------------------------------------------------------------------------------------

function list_user(){
    $SQL = "SELECT * FROM user_list";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    var_dump($response);
    return $response;
}

function is_user_exist($user, $password) {
    $SQL = "SELECT * FROM user_list WHERE username = '$user' and password = '$password'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return false;
    else return true;
}

function is_exist($user){
    $SQL = "SELECT * FROM user_list WHERE username = '$user'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return false;
    else return true;
}

function user_inscription($user, $mdp){
    $SQL = "INSERT INTO user_list VALUES (DEFAULT,'$user','$mdp')";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

//Category

function category_list(){
    //selection
    $SQL = "SELECT category FROM category_list";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    //var_dump($response);
    return $response;
}

//LIST--------------------------------------------------------------------------------------------------

function list_list(){
    $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    //var_dump($response);
    return $response;
}

function list_list3($category, $option, $value){
    /*$SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE category = '$category' ORDER BY list_list.fait, list_list.pin DESC,list_list.creation";*/
    switch ($option) {
        case 'mnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE username = '$value' and category = '$category' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'snotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE visibility = 'shared' and category = '$category' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'pnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE visibility = 'public' and category = '$category' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'fnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE favoris = 1 and category = '$category' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'dnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE fait = 1 and category = '$category' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        default:
            # code...
            break;
    }
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    //var_dump($response);
    return $response;
}

function list_list2($option, $value){
    switch ($option) {
        case 'mnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE username = '$value' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'snotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE visibility = 'shared' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'pnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE visibility = 'public' ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'fnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE favoris = 1 ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        case 'dnotes':
                $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE fait = 1 ORDER BY list_list.fait, list_list.pin DESC, list_list.creation";
            break;
        default:
            # code...
            break;
    }
    /*
    $SQL = "SELECT list,username,icon,category,visibility,color,favoris,pin,fait,reminder,reminderTime FROM list_list INNER JOIN user_list ON list_list.uid = user_list.id INNER JOIN category_list ON category_list.cid = list_list.cid WHERE category = '$category' ORDER BY list_list.fait, list_list.creation";*/
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    //var_dump($response);
    return $response;
}

function add_list_list($newlist, $username, $icon, $category, $visibility, $listcolor, $creation, $modified, $reminder, $reminderTime){
    $id = get_userid($username);
    $cid = get_categoryid($category);
    $SQL = "INSERT INTO list_list VALUES (DEFAULT,$id, $cid,'$newlist', $icon, '$visibility',$listcolor,0,0,0,$creation, $modified, $reminder, $reminderTime)";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

function is_in_list($list){
    $SQL = "SELECT * FROM list_list WHERE list = '$list'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return false;
    else return true;
}

function is_in_element($element){
    $SQL = "SELECT * FROM element_list WHERE element = '$element'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return false;
    else return true;
}

function fav_list($list, $heart){
    $SQL = "UPDATE list_list SET favoris = $heart WHERE list='$list'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

function pin_list($list, $pin){
    $SQL = "UPDATE list_list SET pin = $pin WHERE list='$list'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

function check_list($list, $checked){
    $SQL = "UPDATE list_list SET fait = $checked WHERE list='$list'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

function check_element($element, $listname, $checked){

    $lid = get_id($listname);
    $SQL = "UPDATE element_list SET fait = $checked WHERE element='$element' and list = $lid";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}

function get_userid($username){
    $SQL = "SELECT id FROM user_list WHERE username = '$username'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return -1;
    else {
        foreach($res as $row){
            $id = $row['id'];
        } 
	return $id;
    }
}


function get_id($list){
    $SQL = "SELECT lid FROM list_list WHERE list = '$list'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return -1;
    else {
        foreach($res as $row){
            $id = $row['lid'];
        } 
	return $id;
    }
}

function get_categoryid($category){
    $SQL = "SELECT cid FROM category_list WHERE category = '$category'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    if($res->rowCount() == 0) return 1;
    else {
        foreach($res as $row){
            $id = $row['cid'];
        } 
    return $id;
    }
}

function supp_list($list){
    //trouver le id de list dans list_list
    $lid = get_id($list);
    //supp
    if ($lid!=-1){
        //supprimer element de la liste
	$SQL = "DELETE FROM element_list WHERE list='$lid'";
    	global $db;
    	$res = $db->prepare($SQL);
    	$res->execute();
        //supprimer list
    	$SQL = "DELETE FROM list_list WHERE list='$list'";
    	global $db;
    	$res = $db->prepare($SQL);
    	$res->execute();    }
}

function mod_list($newList, $lastList, $icon, $category, $visibility, $listcolor, $modified, $reminder, $reminderTime){
    //modifier
    $cid = get_categoryid($category);
    $SQL = "UPDATE list_list SET list = '$newList', icon = $icon, cid = $cid, visibility = '$visibility', color = $listcolor, modified = $modified, reminder = $reminder, reminderTime = $reminderTime WHERE list='$lastList'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
}


//ELEMENT------------------------------------------------------------------------------------------------

function list_element($liste){
    //demander id de liste
    $id_liste = get_id($liste);
    //selection
    $SQL = "SELECT element,description,username,ecolor, priority, fait FROM element_list INNER JOIN user_list ON element_list.uid = user_list.id WHERE list = $id_liste ORDER BY element_list.fait, element_list.priority DESC, element_list.creation";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    //var_dump($response);
    return $response;
}

function add_element_list($newlist, $l, $username, $description, $ecolor, $priority, $creation){
    //trouver le id de l dans list_list
    $l_id = get_id($l);
    $uid = get_userid($username);
    //insérer
    if ($l_id!=-1){
    	$SQL = "INSERT INTO element_list VALUES (DEFAULT,'$l_id',$uid,'$newlist', '$description', $ecolor, $priority, $creation, $creation, 0)";
    	global $db;
    	$res = $db->prepare($SQL);
    	$res->execute();
    }
}

function mod_element_list($newElement, $list, $lastElement, $description, $ecolor, $priority, $modified){
    //trouver le id de list dans list_list
    $lid = get_id($list);
    //modifier
    if ($lid!=-1){
        $SQL = "UPDATE element_list SET element = '$newElement', description = '$description', ecolor = $ecolor, priority = $priority, modified = $modified 
                WHERE element = '$lastElement' and list='$lid'";
        global $db;
        $res = $db->prepare($SQL);
        $res->execute();
    }
}

function supp_element_list($supp, $list){
    //trouver le id de list dans list_list
    $lid = get_id($list);
    //supprimer
    if ($lid!=-1){
    	$SQL = "DELETE FROM element_list WHERE element = '$supp' and list='$lid'";
    	global $db;
    	$res = $db->prepare($SQL);
    	$res->execute();
    }
}

function is_in_elementlist($element, $l){
    //trouver le id de l dans list_list
    $l_id = get_id($l);
    //trouver
    if ($l_id == -1) return false;
    else {
        $SQL = "SELECT * FROM element_list WHERE element = '$element' and list='$l_id'";
    	global $db;
    	$res = $db->prepare($SQL);
    	$res->execute();
    	if($res->rowCount() == 0) return false;
    	else return true;
    }
}

//HISTORIQUE------------------------------------------------------------------------------------------------

function addHisto($hisl, $hise, $hisu, $hisa, $hisd){
    $SQL = "INSERT INTO histo VALUES (DEFAULT, '$hisl', '$hise', NOW(), '$hisu', '$hisa','$hisd')";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    return "success";
}

function showhisto(){
    $SQL = "SELECT * FROM histo ORDER BY dateT DESC";//ORDER BY action";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    /*var_dump($response);*/
    return $response;
}

function showhistoES($list){
    $SQL = "SELECT * FROM histo WHERE list = '$list' ORDER BY dateT DESC";//ORDER BY action";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    /*var_dump($response);*/
    return $response;
}

function showhistoEO($element){
    $SQL = "SELECT * FROM histo  WHERE element = '$element' ORDER BY dateT DESC";//ORDER BY action";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    /*var_dump($response);*/
    return $response;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
function historique($h, $liste, $element){
    $SQL = "INSERT INTO historique VALUES (DEFAULT, '$h', NOW(), '$liste', '$element')";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    return "success";
}

function showhistorique(){
    $SQL = "SELECT * FROM historique ORDER BY data DESC";//ORDER BY action";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    $response = $res->fetchAll();
    /*var_dump($response);*/
    return $response;
}

?>
