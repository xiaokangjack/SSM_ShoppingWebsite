<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="/js/kindeditor-4.1.10/themes/default/default.css" type="text/css" rel="stylesheet">
<script type="text/javascript" charset="utf-8" src="/js/kindeditor-4.1.10/kindeditor-all-min.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/kindeditor-4.1.10/lang/zh_CN.js"></script>
<div style="padding:10px 10px 10px 10px">
	<form id="itemAddForm" class="itemForm" method="post">
	    <table cellpadding="5">
	        <tr>
	            <td>商品类目:</td>
	            <td>
	                <!-- href="javascript:void(0)"表示这里表示这个链接不做跳转动作。 -->
	            	<a href="javascript:void(0)" class="easyui-linkbutton selectItemCat">选择类目</a>
	            	<input type="hidden" name="cid" style="width: 280px;"></input>
	            </td>
	        </tr>
	        <tr>
	            <td>商品标题:</td>
	            <td><input class="easyui-textbox" type="text" name="title" data-options="required:true" style="width: 280px;"></input></td>
	        </tr>
	        <tr>
	            <td>商品卖点:</td>
	            <td><input class="easyui-textbox" name="sellPoint" data-options="multiline:true,validType:'length[0,150]'" style="height:60px;width: 280px;"></input></td>
	        </tr>
	        <tr>
	            <td>商品价格:</td>
	            <td><input class="easyui-numberbox" type="text" name="priceView" data-options="min:1,max:99999999,precision:2,required:true" />
	            	<input type="hidden" name="price"/>
	            </td>
	        </tr>
	        <tr>
	            <td>库存数量:</td>
	            <td><input class="easyui-numberbox" type="text" name="num" data-options="min:1,max:99999999,precision:0,required:true" /></td>
	        </tr>
	        <tr>
	            <td>条形码:</td>
	            <td>
	                <input class="easyui-textbox" type="text" name="barcode" data-options="validType:'length[1,30]'" />
	            </td>
	        </tr>
	        <tr>
	            <td>商品图片:</td>
	            <td>
	            	 <a href="javascript:void(0)" class="easyui-linkbutton picFileUpload">上传图片</a>
	                 <input type="hidden" name="image"/>
	            </td>
	        </tr>
	        <tr>
	            <td>商品描述:</td>
	            <td>
	                <textarea style="width:800px;height:300px;visibility:hidden;" name="desc"></textarea>
	            </td>
	        </tr>
	        <tr class="params hide">
	        	<td>商品规格:</td>
	        	<td>
	        		
	        	</td>
	        </tr>
	    </table>
	    <input type="hidden" name="itemParams"/>
	</form>
	<div style="padding:5px">
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">提交</a>
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">重置</a>
	</div>
</div>
<script type="text/javascript">
	var itemAddEditor ;
	$(function(){
		itemAddEditor = ENJOYSHOP.createEditor("#itemAddForm [name=desc]");//查找id为itemAddForm下的name为desc的对象
		var _data = {fun:function(node){
			ENJOYSHOP.changeItemParam(node, "itemAddForm");
		}};
		ENJOYSHOP.init(_data);
	});
	
	function submitForm(){
		//先做校验，因为有些表单是必填项
		if(!$('#itemAddForm').form('validate')){
			$.messager.alert('提示','表单还未填写完成!');
			return ;
		}
		//处理商品的价格的单位，将元转化为分
		$("#itemAddForm [name=price]").val(eval($("#itemAddForm [name=priceView]").val()) * 100);//eval将拿到的数据转为纯数字
		
		//将编辑器中的内容同步到隐藏多行文本中
		//数据实际提交的时候提交的是隐藏的当行文本中的数据
		itemAddEditor.sync();
		
		//输入的规格参数数据保存为json
		var paramJson = [];
		$("#itemAddForm .params li").each(function(i,e){
			var trs = $(e).find("tr");
			var group = trs.eq(0).text();
			var ps = [];
			for(var i = 1;i<trs.length;i++){
				var tr = trs.eq(i);
				ps.push({
					"k" : $.trim(tr.find("td").eq(0).find("span").text()),
					"v" : $.trim(tr.find("input").val())
				});
			}
			paramJson.push({
				"group" : group,
				"params": ps
			});
		});
		paramJson = JSON.stringify(paramJson);
		
		$("#itemAddForm [name=itemParams]").val(paramJson);
		
		/*
		$.post("/rest/item/save",$("#itemAddForm").serialize(), function(data){
			if(data.status == 200){
				$.messager.alert('提示','新增商品成功!');
			}
		});
		*/
		
		//提交到后台的RESTful
/*
jquery中的ajax方法参数:参考博客http://www.cnblogs.com/tylerdonet/p/3520862.html
1.url: 
要求为String类型的参数，（默认为当前页地址）发送请求的地址。
2.type: 
要求为String类型的参数，请求方式（post或get）默认为get。注意其他http请求方法，例如put和delete也可以使用，但仅部分浏览器支持。
3、data: 
要求为Object或String类型的参数，发送到服务器的数据。如果已经不是字符串，
将自动转换为字符串格式。get请求中将附加在url后。防止这种自动转换，
可以查看processData选项。对象必须为key/value格式，
例如{foo1:"bar1",foo2:"bar2"}转换为&foo1=bar1&foo2=bar2。
如果是数组，JQuery将自动为不同值对应同一个名称。
例如{foo:["bar1","bar2"]}转换为&foo=bar1&foo=bar2。	
*/
		$.ajax({
		   type: "POST",
		   url: "/rest/item",
		   data: $("#itemAddForm").serialize(),
		   //数据形式为json格式的序列化数据
/*下面是一个实际的商品提交数据，可以看出提交的数据形式是xxx&xxx&xx的形式。
cid，title，sellPoint等参数名与上面表单源码中的“name”属性值对应。
cid=76&title=test&sellPoint=good&priceView=222.00&price=22200&
num=333&barcode=4444&image=http%3A%2F%2Fimage.ENJOYSHOP.com%2
Fimages%2F2017%2F04%2F24%2F2017042408295162105014.jpg&desc=%E5%8D%A
1%E5%B0%B1%E6%98%AF%E5%A4%9A%E7%9C%8B%E5%93%88%E5%9C%A3%E8%AF%9E%E8%
8A%82%E5%95%8A%E6%98%AF%E7%9A%84&itemParams=%5B%5D
 */
		   statusCode :{
			   201 : function(){
				   $.messager.alert('提示','新增商品成功!');
			   },
			   400 : function(){
				   $.messager.alert('提示','提交的参数不合法!');
			   },
			   500 : function(){
				   $.messager.alert('提示','新增商品失败!');
			   }
		   }
		});
	}
	
	function clearForm(){
		$('#itemAddForm').form('reset');
		itemAddEditor.html('');
	}
</script>
