

pdf("/Users/mojtababagherzadeh/analysistime.pdf", 
    useDingbats=FALSE, width=6, height=4)
ggplot(analysis_time, aes(x = model, y = `time`/1000)) +
  geom_boxplot(outlier.shape=NA,width=.5)+
  scale_y_continuous(
    breaks = seq(0, 5, .5),
    limits=c(0, 5)) + #geom_jitter(size = 1,width=.25,col=rgb(0,0,0,.1))+
  theme_bw()+
   #theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  scale_x_discrete(labels = get_wraper(10))+
  xlab("Models") + ylab("Analysis Time (Seconds)")
dev.off()

pdf("/Users/mojtababagherzadeh/refinmenttime.pdf", 
    useDingbats=FALSE, width=6, height=4)
ggplot(refinment_time, aes(x = model, y = `time`/1000)) +
  geom_boxplot(outlier.shape=NA,width=.5)+
  scale_y_continuous(
    breaks = seq(0, 32, 4),
    limits=c(0, 32)) + #geom_jitter(size = 1,width=.25,col=rgb(0,0,0,.1))+
  theme_bw()+
  #theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  #scale_x_discrete(labels = get_wraper(10))+
  xlab("Models") + ylab("Analysis Time (Seconds)")
dev.off()

refinment_overhead_melt<-melt(refinment_overhead, refinedversion=(c("Load", "Code generation","Save")))
ggplot(refinment_overhead_melt, aes(x = variable, y = (value-1))) +
  geom_boxplot(outlier.shape=NA,width=.5)+
  scale_y_continuous(
    breaks = seq(0, 5, 1),
    limits=c(0, 5)) + #geom_jitter(size = 1,width=.25,col=rgb(0,0,0,.1))+
  theme_bw()+
  #theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  scale_x_discrete(labels = get_wraper(10))+
  xlab("Models") + ylab("Analysis Time (Seconds)")


positions <- c("Org.", "10%", "20%","30%","40%","60%","50%","70%","80%","90%")
pdf("/Users/mojtababagherzadeh/addedstate.pdf", 
    useDingbats=FALSE, width=4, height=3)
ggplot(added_complexity, aes(x = refined_version, y = `state-ratio`*100)) +
  geom_boxplot(outlier.shape=NA,width=.5)+
  scale_y_continuous(
    breaks = seq(0, 600, 50),
    limits=c(0, 500)) + #geom_jitter(size = 1,width=.25,col=rgb(0,0,0,.1))+
  theme_bw()+
  #theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  scale_x_discrete(limits = positions)+
  xlab("Model Versions") + ylab("% of Added States")
dev.off()

pdf("/Users/mojtababagherzadeh/addedtrans.pdf", 
    useDingbats=FALSE, width=4, height=3)
ggplot(added_complexity, aes(x = refined_version, y = `transition-ratio`*100)) +
  geom_boxplot(outlier.shape=NA,width=.5)+
  scale_y_continuous(
    breaks = seq(0, 600, 50),
    limits=c(0, 500)) + #geom_jitter(size = 1,width=.25,col=rgb(0,0,0,.1))+
  theme_bw()+
  #theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  scale_x_discrete(limits = positions)+
  xlab("Model Versions") + ylab("% of Added Transtions")
dev.off()


median(added_complexity[added_complexity$refined_version=='Org.',]$`transition-ratio`)*100
median(added_complexity[added_complexity$refined_version=='90%',]$`transition-ratio`)*100

median(added_complexity[added_complexity$refined_version=='Org.',]$`state-ratio`)*100
median(added_complexity[added_complexity$refined_version=='90%',]$`state-ratio`)*100










median(refinment_time[refinment_time$model=='ParcelRouter',]$`time`)/1000
median(refinment_time[refinment_time$model=='CarDoorLock',]$`time`)/1000
median(refinment_time[refinment_time$model=='Rover',]$`time`)/1000
median(refinment_time[refinment_time$model=='DigitalWatch',]$`time`)/1000
median(refinment_time[refinment_time$model=='Replication',]$`time`)/1000
median(refinment_time[refinment_time$model=='Debuggable\nReplication',]$`time`)/1000















max(refinment_time[refinment_time$model=='ParcelRouter',]$`time`)/1000
max(refinment_time[refinment_time$model=='CarDoorLock',]$`time`)/1000
max(refinment_time[refinment_time$model=='Rover',]$`time`)/1000
max(refinment_time[refinment_time$model=='DigitalWatch',]$`time`)/1000
max(refinment_time[refinment_time$model=='Replication',]$`time`)/1000
max(refinment_time[refinment_time$model=='Debuggable\nReplication',]$`time`)/1000


min(refinment_time[refinment_time$model=='ParcelRouter',]$`time`)/1000
min(refinment_time[refinment_time$model=='CarDoorLock',]$`time`)/1000
min(refinment_time[refinment_time$model=='Rover',]$`time`)/1000
min(refinment_time[refinment_time$model=='DigitalWatch',]$`time`)/1000
min(refinment_time[refinment_time$model=='Replication',]$`time`)/1000
min(refinment_time[refinment_time$model=='Debuggable\nReplication',]$`time`)/1000


mean(refinment_time[refinment_time$model=='ParcelRouter',]$`time`)/1000
mean(refinment_time[refinment_time$model=='CarDoorLock',]$`time`)/1000
mean(refinment_time[refinment_time$model=='Rover',]$`time`)/1000
mean(refinment_time[refinment_time$model=='DigitalWatch',]$`time`)/1000
mean(refinment_time[refinment_time$model=='Replication',]$`time`)/1000
mean(refinment_time[refinment_time$model=='Debuggable\nReplication',]$`time`)/1000



mean(analysis_time[analysis_time$model=='ParcelRouter',]$`time`)/1000
mean(analysis_time[analysis_time$model=='CarDoorLock',]$`time`)/1000
mean(analysis_time[analysis_time$model=='Rover',]$`time`)/1000
mean(analysis_time[analysis_time$model=='DigitalWatch',]$`time`)/1000
mean(analysis_time[analysis_time$model=='Replication',]$`time`)/1000
mean(analysis_time[analysis_time$model=='Debuggable\nReplication',]$`time`)/1000



max(analysis_time[analysis_time$model=='ParcelRouter',]$`time`)/1000
max(analysis_time[analysis_time$model=='CarDoorLock',]$`time`)/1000
max(analysis_time[analysis_time$model=='Rover',]$`time`)/1000
max(analysis_time[analysis_time$model=='DigitalWatch',]$`time`)/1000
max(analysis_time[analysis_time$model=='Replication',]$`time`)/1000
max(analysis_time[analysis_time$model=='Debuggable\nReplication',]$`time`)/1000


min(analysis_time[analysis_time$model=='ParcelRouter',]$`time`)/1000
min(analysis_time[analysis_time$model=='CarDoorLock',]$`time`)/1000
min(analysis_time[analysis_time$model=='Rover',]$`time`)/1000
min(analysis_time[analysis_time$model=='DigitalWatch',]$`time`)/1000
min(analysis_time[analysis_time$model=='Replication',]$`time`)/1000
min(analysis_time[analysis_time$model=='Debuggable\nReplication',]$`time`)/1000


median(analysis_time[analysis_time$model=='ParcelRouter',]$`time`)/1000
median(analysis_time[analysis_time$model=='CarDoorLock',]$`time`)/1000
median(analysis_time[analysis_time$model=='Rover',]$`time`)/1000
median(analysis_time[analysis_time$model=='DigitalWatch',]$`time`)/1000
median(analysis_time[analysis_time$model=='Replication',]$`time`)/1000
median(analysis_time[analysis_time$model=='Debuggable\nReplication',]$`time`)/1000
