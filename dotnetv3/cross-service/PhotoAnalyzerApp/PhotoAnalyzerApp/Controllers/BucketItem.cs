/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace PhotoAnalyzerApp.Controllers

{
    public class BucketItem
    {
        private String key;
        private String owner;
        private String size;

        public void setSize(String size)
        {
            this.size = size;
        }

        public String getSize()
        {
            return this.size;
        }
    
        public void setOwner(String owner)
        {
            this.owner = owner;
        }

        public String getOwner()
        {
            return this.owner;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public String getKey()
        {
            return this.key;
        }
    }
}
