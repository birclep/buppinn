class EquipmentRegistrationController < ApplicationController
  
require 'open3'
require "fileutils"
after_filter :make

def make(groupid)
    @list = groupid.to_s
    @equipment_classes = EquipmentClass.find_all_by_group_id(groupid)
    @equipment_classes.each.with_index(1) do |set, i|
      @list += " "+set.equipment_classes_name
      @list += " "+set.id.to_s
    end
    @list +=" "+"end"
    @picfile = Dir.glob("/home/sit-user-15/rails_test/bircle_server3/2/*.jpg")
    @picfile.each.with_index(1) do |pic, i|
      @list += " "+pic
    end
    puts @list
    o, e, s = Open3.capture3("/home/sit-user-15/recognizertest/make " + @list)
    @error = e
    puts @error
end

def copy(from ,to)
    open(from){|input|
        open(to, "w"){|output|
            output.write(input.read)
        }
    }
end

 def getjson
   equipmentclassname=params[:_json].first[:equipmentname]
   equipmentnum=params[:_json].first[:registernumber]
   userid=params[:_json].first[:userid]
   groupid=params[:_json].first[:groupid]
  
     newequipmentclass=EquipmentClass.new
     newequipmentclass.group_id=groupid
     newequipmentclass.equipment_classes_name=equipmentclassname
     newequipmentclass.save
  
      

   equipmentinfo=params[:_json]
   
   equipmentinfo.each do |_json|
   newequipment=Equipment.new
   newequipment.equipment_class_id=newequipmentclass.id
   newequipment.equipment_location=_json["loc"]
   newequipment.unique_id=_json["id"]
   newequipment.equipment_recorder_id=_json["userid"]
   newequipment.descr=_json["desc"]
   newequipment.state=0
   
   newequipment.save
    end
  @file2 = Dir.glob("/home/sit-user-15/rails_test/bircle_server3/2/*.jpg").sort_by {|f| File.mtime(f)}.reverse[0]
  @cpfile = "/home/sit-user-15/rails_test/bircle_server3/app/assets/images/"
  @cpfile += equipmentclassname.to_s
  @cpfile += "0.jpg"
  puts @cpfile
  puts "1"
  puts @file2
  copy(@file2, @cpfile)
  @equipment_check = EquipmentClass.find_all_by_group_id(session[:group_id])
  if @equipment_check.length > 0
   make(groupid)
  end
  end



 def getpictures
  name=params[:NAME]
  userid=params[:USERID]
  groupid=params[:GROUPID]
  puts "File received NAME="+ name
      data=params[:file]
      uptime=DateTime.now
      uploadfilename=data.original_filename

      File.open(Rails.root.join(groupid,uploadfilename),'wb') do |of|
                               of.write(data.read)
  end
 end
end