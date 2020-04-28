select * from profiling  
where script="RefineForPMD.eol"
el = "Debuggable\nReplication"
where model ="DebuggableReplication" 


is null and modelversion like "%DigitalWatch%" 


delete from profiling where script = "PartialModelCreator.eol"  


select model,modelversion,script,avg(`time`),max(`time`),min(`time`) from profiling 
group by model,modelversion,script 



select model,count(*) from activity_time 
group by model

(select a.model,a.modelversion,b.modelversion,(a.loading-b.loading), (a.loading-b.loading)/b.loading*100 ,(a.`generation`-b.`generation`),  
			(a.`generation`-b.`generation`)/b.generation*100 , 	(a.saving-b.saving), 
			(a.saving-b.saving)/b.saving*100 from activity_time2 a , activity_time2 b 
where a.model=b.model and a.modelversion = concat("Refined_",b.modelversion))

union 

(select a.model,a.modelversion,b.modelversion,(a.loading-b.loading), (a.loading-b.loading)/b.loading*100 ,(a.`generation`-b.`generation`),  
			(a.`generation`-b.`generation`)/b.generation*100 , 	(a.saving-b.saving), 
			(a.saving-b.saving)/b.saving*100 from activity_time a , activity_time b 
where a.model=b.model and a.modelversion = concat("Refined_",b.modelversion)) 


select a.model,a.modelversion,b.modelversion, a.loading/b.loading ,  a.`generation`/b.`generation` , a.saving/b.saving from activity_time a , activity_time b 
where a.model=b.model and a.modelversion = concat("Refined_",b.modelversion)

select a.model,a.modelversion,b.modelversion, a.loading/b.loading ,  a.`generation`/b.`generation` , a.saving/b.saving from activity_time2 a , activity_time2 b 
where a.model=b.model and a.modelversion = concat("Refined_",b.modelversion)

create table complexity_metric as (
select model,modelversion, sum(states),sum(transitions) from model_size 
group by model,modelversion )

select a.`modelversion`, b.`modelversion` , a.states/(b.states+1) , a.transitions/(b.transitions+1) from `complexity_metric`   a , `complexity_metric`   b
where a.model=b.model and a.modelversion = concat("Refined_",b.modelversion)

select a.model,a.`modelversion`, b.`modelversion` , a.states/(b.states) , a.transitions/(b.transitions) from model_size   a , model_size   b
where not (a.states=0 and a.transitions=0) and a.model=b.model and a.modelversion = concat("Refined_",b.modelversion) and a.capsule=b.capsule