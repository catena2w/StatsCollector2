
set terminal pngcairo
set datafile separator ","


################

filename='wavesTopMiner.csv'
set output "wavesTopMiner.png"
set xlabel "{/*1.5 Time}"
set ylabel "{/*1.5 Top Miner, %}" rotate by 90 offset -0.1,0

set xlabel "Date"
set timefmt "%s"
set format x "%m/%y"
set xdata time
#set xtics  0,40000,150000

set xtics rotate by 90
set xtics font ", 12"
set xtics offset 0,-2.1
set ytics font ", 12"

binwidth=500
bin(x,width)=width*floor(x/width)

plot filename using 1:2  notitle smooth freq with boxes

################

filename='nxtTopMiner.csv'
set output "nxtTopMiner.png"
set xlabel "{/*1.5 Time}"
set ylabel "{/*1.5 Top Miner, %}" rotate by 90 offset -0.1,0

set xlabel "Date"
set timefmt "%s"
set format x "%m/%y"
set xdata time
#set xtics  0,40000,150000

set xtics rotate by 90
set xtics font ", 12"
set xtics offset 0,-2.1
set ytics font ", 12"

binwidth=500
bin(x,width)=width*floor(x/width)

plot filename using 1:2  notitle smooth freq with boxes

################

filename='nxtActiveStake.csv'
set output "nxtActiveStake.png"
set xlabel "{/*1.5 Time}"
set ylabel "{/*1.5 Active Stake, %}" rotate by 90 offset -0.1,0

set xlabel "Date"
set timefmt "%s"
set format x "%m/%y"
set xdata time

set xtics rotate by 90
set xtics font ", 12"
set xtics offset 0,-2.1
set ytics font ", 12"

binwidth=500
bin(x,width)=width*floor(x/width)

plot filename using 1:2  notitle smooth freq with boxes

################

filename='wavesActiveStake.csv'
set output "wavesActiveStake.png"
set xlabel "{/*1.5 Time}"
set ylabel "{/*1.5 Active Stake, %}" rotate by 90 offset -0.1,0

set xlabel "Date"
set timefmt "%s"
set format x "%m/%y"
set xdata time

set xtics rotate by 90
set xtics font ", 12"
set xtics offset 0,-2.1
set ytics font ", 12"

binwidth=500
bin(x,width)=width*floor(x/width)

plot filename using 1:2  notitle smooth freq with boxes
