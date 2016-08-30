    func main() {
        svc := ec2.New(session.New())
        opts := &ec2.CreateImageInput{
            Description: aws.String("image description"),
            InstanceId:  aws.String("i-abcdef12"),
            Name:        aws.String("image name"),
            BlockDeviceMappings: []*ec2.BlockDeviceMapping{
                &ec2.BlockDeviceMapping{
                    DeviceName: aws.String("/dev/sda1"),
                    NoDevice:    aws.String(""),
                },
                &ec2.BlockDeviceMapping{
                    DeviceName: aws.String("/dev/sdb"),
                    NoDevice:    aws.String(""),
                },
                &ec2.BlockDeviceMapping{
                    DeviceName: aws.String("/dev/sdc"),
                    NoDevice:    aws.String(""),
                },
            },
        }
        resp, err := svc.CreateImage(opts)
        if err != nil {
            fmt.Println(err)
            return
        }

        fmt.Println("success", resp)
    }